package io.yogurt.cli_mini_game.user.service;

import static java.util.Objects.isNull;

import io.yogurt.cli_mini_game.common.user.dto.LoginRequest;
import io.yogurt.cli_mini_game.common.user.dto.StoreUserRequest;
import io.yogurt.cli_mini_game.common.user.dto.UserInfoResponse;
import io.yogurt.cli_mini_game.exception.BadRequestException;
import io.yogurt.cli_mini_game.user.entity.User;
import io.yogurt.cli_mini_game.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserInfoResponse store(StoreUserRequest request) {

        if (userRepository.existsByUsername(request.username())) {

            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + request.username());
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 사용자 생성 및 저장
        User user = userRepository.save(
            new User(
                request.username(),
                encodedPassword,
                request.nickname()
            )
        );

        log.info("회원가입 성공: {}", user.getUsername());
        return new UserInfoResponse(
            user.getId(),
            user.getNickname(),
            user.getUsername()
        );
    }


    public UserInfoResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username());

        if (isNull(user)) {

            log.warn("로그인 실패: 존재하지 않는 사용자 - {}", request.username());
            throw new BadRequestException("존재하지 않는 사용자 입니다.", "NOT_FOUND");
        }

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {

            log.warn("로그인 실패: 비밀번호 불일치 - {}", request.username());
            throw new BadRequestException("비밀번호 불일치", "INVALID_PASSWORD");
        }

        log.info("로그인 성공: {}", request.username());
        return new UserInfoResponse(
            user.getId(),
            user.getNickname(),
            user.getUsername()
        );
    }
}
