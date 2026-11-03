package com.grepp.efy.domain.credit.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

// 토스페이먼츠 결제 승인 API를 호출한다
@Component
public class TossPaymentsClient {

    private final RestClient restClient;

    // 시크릿 키가 없어도 서버는 실행되고, 승인 호출만 실패한다
    public TossPaymentsClient(@Value("${toss.secret-key:}") String secretKey) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(10)); // 응답이 없으면 무한정 기다리지 않는다

        // 토스 인증: "시크릿키:"를 Base64로 인코딩한 Basic 인증
        String credentials = Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
        this.restClient = RestClient.builder()
                .baseUrl("https://api.tosspayments.com/v1")
                .requestFactory(requestFactory)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + credentials)
                .build();
    }

    // 성공하면 그대로 끝난다
    // 토스가 4xx·5xx로 거절하면 RestClientResponseException, 응답이 없으면 ResourceAccessException을 던진다
    public void confirm(String paymentKey, String orderId, long amount) {
        restClient.post()
                .uri("/payments/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("paymentKey", paymentKey, "orderId", orderId, "amount", amount))
                .retrieve()
                .toBodilessEntity();
    }
}