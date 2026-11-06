package com.grepp.efy.domain.file;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    // 삭제되지 않은 파일만 조회
    Optional<UploadFile> findByIdAndDeletedAtIsNull(Long id);
}
