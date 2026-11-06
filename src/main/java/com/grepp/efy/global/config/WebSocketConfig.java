package com.grepp.efy.global.config;

import com.grepp.efy.global.security.StompAuthInterceptor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@NullMarked
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthInterceptor stompAuthInterceptor;
    private final String endpoint;
    private final String applicationPrefix;
    private final String brokerPrefix;
    private final String frontendUrl;

    public WebSocketConfig(
            StompAuthInterceptor stompAuthInterceptor,
            @Value("${app.websocket.endpoint}") String endpoint,
            @Value("${app.websocket.application-prefix}") String applicationPrefix,
            @Value("${app.websocket.broker-prefix}") String brokerPrefix,
            @Value("${app.frontend-url}") String frontendUrl
    ) {
        this.stompAuthInterceptor = stompAuthInterceptor;
        this.endpoint = endpoint;
        this.applicationPrefix = applicationPrefix;
        this.brokerPrefix = brokerPrefix;
        this.frontendUrl = frontendUrl;
    }

    // 연결 주소. 허용 Origin은 REST CORS 설정과 같은 프론트 주소
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(endpoint)
                .setAllowedOrigins(frontendUrl);
    }

    // /pub으로 보낸 메시지는 @MessageMapping으로, /sub 구독은 메모리 기반 Simple Broker가 전달
    // /queue는 @SendToUser로 특정 세션에만 보내는 메시지 (/user/queue/errors → 세션별 /queue/errors-user{id})
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(applicationPrefix);
        registry.enableSimpleBroker(brokerPrefix, "/queue");
    }

    // 클라이언트가 보내는 모든 STOMP 프레임은 인증 인터셉터를 먼저 거친다 (CONNECT에서만 검사)
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthInterceptor);
    }
}
