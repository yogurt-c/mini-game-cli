package io.yogurt.cli_mini_game.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // CSRF 비활성화 (개발 환경)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/game-websocket/**").permitAll()  // WebSocket 허용
                .requestMatchers("/h2-console/**").permitAll()       // H2 콘솔 허용
                .anyRequest().permitAll()                            // 나머지 모두 허용 (개발용)
            )
            .headers(headers -> headers
                .frameOptions(FrameOptionsConfig::sameOrigin)  // H2 콘솔 iframe 허용
            );

        return http.build();
    }
}
