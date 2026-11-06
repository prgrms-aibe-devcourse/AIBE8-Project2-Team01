package com.grepp.efy.domain.file;

import com.grepp.efy.domain.file.dto.FileUploadResponse;
import com.grepp.efy.domain.user.member.Member;
import com.grepp.efy.domain.user.member.MemberRepository;
import com.grepp.efy.global.exception.BusinessException;
import com.grepp.efy.global.exception.ErrorCode;
import com.grepp.efy.global.storage.SupabaseStorageClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

// 여러 파일을 한 번에 올린다. 하나라도 실패하면 전부 실패 (이미 올린 파일은 지우고 DB에도 남기지 않는다)
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadFileService {

    private static final int MAX_FILE_COUNT = 10;

    // 허용 형식: 확장자 → 짝이 맞는 Content-Type
    // 이 목록에 없는 확장자, 짝이 맞지 않는 Content-Type은 모두 거절한다
    private static final Map<String, Set<String>> ALLOWED_TYPES = Map.of(
            "jpg", Set.of("image/jpeg"),
            "jpeg", Set.of("image/jpeg"),
            "png", Set.of("image/png"),
            "webp", Set.of("image/webp"),
            "gif", Set.of("image/gif"),
            "heic", Set.of("image/heic", "image/heif"),
            "pdf", Set.of("application/pdf")
    );

    private final UploadFileRepository uploadFileRepository;
    private final MemberRepository memberRepository;
    private final SupabaseStorageClient storageClient;

    @Transactional
    public List<FileUploadResponse> uploadFiles(Long memberId, List<MultipartFile> files, UploadFileVisibility visibility) {
        // 1. 업로드 전에 전부 검사 (크기 초과는 컨트롤러 진입 전에 GlobalExceptionHandler가 잡는다)
        validate(files);
        Member uploader = memberRepository.getReferenceById(memberId); // SELECT 없이 연관만 건다

        // 2. 파일마다 UUID 저장 경로를 만든다
        List<PendingFile> pendings = files.stream()
                .map(f -> new PendingFile(f, createStoragePath(f.getOriginalFilename())))
                .toList();

        // 3. 하나씩 Supabase에 업로드. 중간에 실패하면 앞에서 올린 것을 지운다
        List<String> uploaded = new ArrayList<>();
        for (PendingFile p : pendings) {
            try {
                storageClient.upload(visibility, p.storagePath(), p.file().getContentType(), p.file().getBytes());
                uploaded.add(p.storagePath());
            } catch (IOException | RestClientException e) {
                log.error("Supabase 업로드 실패 path={}", p.storagePath(), e);
                deleteAllQuietly(visibility, uploaded);
                throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED_EXCEPTION);
            }
        }

        // 4. DB 저장 + URL 생성. 실패하면 올린 파일을 전부 지운다
        // saveAll은 INSERT가 커밋 때 나갈 수 있어서, 바로 INSERT하는 saveAllAndFlush를 쓴다
        // PRIVATE URL 생성도 Supabase 요청이라 실패할 수 있어서 try 안에 둔다
        try {
            List<UploadFile> saved = uploadFileRepository.saveAllAndFlush(
                    pendings.stream().map(p -> toEntity(p, uploader, visibility)).toList());
            return saved.stream()
                    .map(f -> FileUploadResponse.of(f, storageClient.createUrl(f.getVisibility(), f.getStoragePath())))
                    .toList();
        } catch (RuntimeException e) {
            deleteAllQuietly(visibility, uploaded);
            throw e;
        }
    }

    // 다른 도메인에서 fileId로 파일을 연결할 때 사용
    // 삭제된 파일은 제외하고 없으면 FILE_NOT_FOUND_EXCEPTION 발생
    public UploadFile getUploadFileEntity(Long fileId) {
        return uploadFileRepository.findByIdAndDeletedAtIsNull(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND_EXCEPTION));
    }

    // 본인이 업로드한 파일만 반환하고 다른 사용자의 파일이면 FILE_FORBIDDEN_EXCEPTION 발생
    // uploader는 LAZY지만 id만 꺼내서 추가 SELECT가 나가지 않는다
    public UploadFile getMyUploadFileEntity(Long fileId, Long memberId) {
        UploadFile file = getUploadFileEntity(fileId);
        if (!file.getUploader().getId().equals(memberId)) {
            throw new BusinessException(ErrorCode.FILE_FORBIDDEN_EXCEPTION);
        }
        return file;
    }

    private void validate(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY_EXCEPTION);
        }
        if (files.size() > MAX_FILE_COUNT) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED_EXCEPTION);
        }
        // 하나라도 0바이트이거나 허용하지 않는 형식이면 전부 거절
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_EMPTY_EXCEPTION);
            }
            validateType(file);
        }
    }

    // 확장자와 Content-Type이 둘 다 허용 목록에 있고 서로 짝이 맞아야 한다
    private void validateType(MultipartFile file) {
        String ext = extractExtension(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (ext == null || contentType == null) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE_EXCEPTION);
        }
        // "image/jpeg; charset=..." 처럼 뒤에 붙는 값은 떼고 소문자로 비교
        String mimeType = contentType.split(";")[0].trim().toLowerCase();
        Set<String> allowed = ALLOWED_TYPES.get(ext);
        if (allowed == null || !allowed.contains(mimeType)) {
            throw new BusinessException(ErrorCode.FILE_INVALID_TYPE_EXCEPTION);
        }
    }

    // 마지막 점 뒤를 소문자로 돌려준다. 확장자가 없으면 null ("README", "file." 등)
    private String extractExtension(String originalName) {
        if (originalName == null) {
            return null;
        }
        int dot = originalName.lastIndexOf(".");
        if (dot < 0 || dot == originalName.length() - 1) {
            return null;
        }
        return originalName.substring(dot + 1).toLowerCase();
    }

    // UUID + 소문자 확장자 (원래 이름은 DB에만 보관)
    // validate()를 통과한 뒤라 확장자는 항상 허용 목록 안의 값이다
    private String createStoragePath(String originalName) {
        return UUID.randomUUID() + "." + extractExtension(originalName);
    }

    private UploadFile toEntity(PendingFile p, Member uploader, UploadFileVisibility visibility) {
        UploadFile file = new UploadFile();
        file.setUploader(uploader);
        file.setStoragePath(p.storagePath());
        file.setOriginalName(p.file().getOriginalFilename());
        file.setContentType(p.file().getContentType());
        file.setSize(p.file().getSize());
        file.setVisibility(visibility);
        return file;
    }

    // 하나 삭제에 실패해도 나머지는 계속 지운다. 실패한 건 로그로 남겨서 나중에 정리한다
    private void deleteAllQuietly(UploadFileVisibility visibility, List<String> storagePaths) {
        for (String path : storagePaths) {
            try {
                storageClient.delete(visibility, path);
            } catch (RestClientException e) {
                log.error("보상 삭제 실패 path={}", path, e);
            }
        }
    }

    // 파일 하나와 그 파일의 저장 경로를 묶어 둔다
    private record PendingFile(MultipartFile file, String storagePath) {}
}
