package io.yogurt.cli_mini_game.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket + STOMP 설정
 * <p>
 * STOMP (Simple Text Oriented Messaging Protocol): - WebSocket 위에서 동작하는 메시징 프로토콜 - Pub/Sub 패턴 지원 -
 * 메시지 라우팅 기능 제공
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * STOMP 엔드포인트 등록 클라이언트가 WebSocket에 연결할 때 사용하는 URL
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/game-websocket")  // ws://localhost:8080/game-websocket
            .setAllowedOrigins("*");         // CORS 설정 (모든 origin 허용)
        // .withSockJS();  // SockJS fallback (선택사항, 브라우저 호환성 위해)
    }

    /**
     * 메시지 브로커 설정
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트 → 서버로 메시지 전송 시 prefix
        // 예: /app/game/join
        registry.setApplicationDestinationPrefixes("/app");

        // 서버 → 클라이언트로 메시지 브로드캐스트 시 prefix
        // /topic: 1:N (브로드캐스트)
        // /queue: 1:1 (개인 메시지)
        // 예: /topic/game/123, /queue/user/errors
        registry.enableSimpleBroker("/topic", "/queue");
    }
}