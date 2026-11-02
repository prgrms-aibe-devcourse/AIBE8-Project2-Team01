package com.grepp.efy.domain.file.dto;

import com.grepp.efy.domain.file.UploadFile;
import com.grepp.efy.domain.file.UploadFileVisibility;

import java.time.LocalDateTime;

public record FileUploadResponse(
        Long fileId,
        String originalName,
        String contentType,
        Long size,
        UploadFileVisibility visibility,
        String url,
        LocalDateTime uploadedAt
) {
    public static FileUploadResponse of(UploadFile file, String url) {
        return new FileUploadResponse(file.getId(), file.getOriginalName(), file.getContentType(),
                file.getSize(), file.getVisibility(), url, file.getCreatedAt());
    }
}
