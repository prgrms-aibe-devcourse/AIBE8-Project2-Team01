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
import java.util.UUID;

// 여러 파일을 한 번에 올린다. 하나라도 실패하면 전부 실패 (이미 올린 파일은 지우고 DB에도 남기지 않는다)
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UploadFileService {

    private static final int MAX_FILE_COUNT = 10;

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

    private void validate(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_EMPTY_EXCEPTION);
        }
        if (files.size() > MAX_FILE_COUNT) {
            throw new BusinessException(ErrorCode.FILE_COUNT_EXCEEDED_EXCEPTION);
        }
        // 하나라도 0바이트면 전부 거절
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                throw new BusinessException(ErrorCode.FILE_EMPTY_EXCEPTION);
            }
        }
    }

    // UUID + 원래 확장자 (원래 이름은 DB에만 보관)
    private String createStoragePath(String originalName) {
        String ext = "";
        if (originalName != null && originalName.contains(".")) {
            ext = originalName.substring(originalName.lastIndexOf("."));
        }
        return UUID.randomUUID() + ext;
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
