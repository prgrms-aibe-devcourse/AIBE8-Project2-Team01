package com.grepp.efy.global.storage;

import com.grepp.efy.domain.file.UploadFileVisibility;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

// Supabase Storage REST API를 호출하는 클라이언트
// 경로는 uri 변수로 넣지 않고 문자열로 이어 붙인다 (변수로 넣으면 경로 안의 /가 %2F로 바뀐다)
@Component
public class SupabaseStorageClient {

    private static final int SIGNED_URL_EXPIRES_IN = 3600; // 비공개 파일 URL 유효 시간 (초)

    private final RestClient restClient;
    private final String supabaseUrl;
    private final String publicBucket;
    private final String privateBucket;

    public SupabaseStorageClient(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.service-key}") String serviceKey,
            @Value("${supabase.bucket.public}") String publicBucket,
            @Value("${supabase.bucket.private}") String privateBucket) {
        this.supabaseUrl = supabaseUrl;
        this.publicBucket = publicBucket;
        this.privateBucket = privateBucket;
        // 모든 요청에 붙는 공통 헤더를 한 번만 설정
        this.restClient = RestClient.builder()
                .baseUrl(supabaseUrl + "/storage/v1")
                .defaultHeader("Authorization", "Bearer " + serviceKey)
                .defaultHeader("apikey", serviceKey)
                .build();
    }

    // 4xx/5xx 응답이면 RestClientException을 던진다
    public void upload(UploadFileVisibility visibility, String storagePath, String contentType, byte[] bytes) {
        restClient.post()
                .uri("/object/" + bucket(visibility) + "/" + storagePath)
                .contentType(MediaType.parseMediaType(contentType))
                .body(bytes)
                .retrieve()
                .toBodilessEntity();
    }

    public void delete(UploadFileVisibility visibility, String storagePath) {
        restClient.delete()
                .uri("/object/" + bucket(visibility) + "/" + storagePath)
                .retrieve()
                .toBodilessEntity();
    }

    // 공개 파일은 고정 URL, 비공개 파일은 1시간짜리 만료 URL
    public String createUrl(UploadFileVisibility visibility, String storagePath) {
        if (visibility == UploadFileVisibility.PUBLIC) {
            return supabaseUrl + "/storage/v1/object/public/" + publicBucket + "/" + storagePath;
        }
        SignedUrlResponse res = restClient.post()
                .uri("/object/sign/" + privateBucket + "/" + storagePath)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("expiresIn", SIGNED_URL_EXPIRES_IN))
                .retrieve()
                .body(SignedUrlResponse.class);
        // 응답은 "/object/sign/..." 처럼 앞부분이 빠진 경로로 온다
        return supabaseUrl + "/storage/v1" + res.signedURL();
    }

    private String bucket(UploadFileVisibility visibility) {
        return visibility == UploadFileVisibility.PUBLIC ? publicBucket : privateBucket;
    }

    private record SignedUrlResponse(String signedURL) {}
}
