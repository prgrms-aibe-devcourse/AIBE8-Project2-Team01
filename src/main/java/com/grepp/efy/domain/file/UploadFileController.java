package com.grepp.efy.domain.file;

import com.grepp.efy.domain.file.dto.FileUploadResponse;
import com.grepp.efy.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UploadFileController {
    private final UploadFileService uploadFileService;

    // files가 없으면 스프링 대신 서비스 validate()가 FILE_EMPTY(400)로 응답하도록 required = false
    @PostMapping(value = "/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<FileUploadResponse>> uploadFiles(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam("visibility") UploadFileVisibility visibility) {
        List<FileUploadResponse> response = uploadFileService.uploadFiles(userDetails.getMemberId(), files, visibility);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
