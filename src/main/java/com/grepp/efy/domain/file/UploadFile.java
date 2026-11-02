package com.grepp.efy.domain.file;

import com.grepp.efy.domain.user.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file")
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "uploader_id", nullable = false)
    private Member uploader;

    @Column(nullable = false, unique = true)
    private String storagePath; // Supabase 저장 경로 (URL 아님, UUID 기반)

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false, length = 100)
    private String contentType;

    @Column(nullable = false)
    private Long size; // 바이트

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private UploadFileVisibility visibility; // PUBLIC, PRIVATE

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime deletedAt; // 값이 있으면 삭제된 파일

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
