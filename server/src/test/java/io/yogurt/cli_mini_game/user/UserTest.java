package io.yogurt.cli_mini_game.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yogurt.cli_mini_game.common.user.dto.LoginRequest;
import io.yogurt.cli_mini_game.common.user.dto.StoreUserRequest;
import io.yogurt.cli_mini_game.user.entity.User;
import io.yogurt.cli_mini_game.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공 - 유효한 사용자 정보로 회원가입")
    void store_success() throws Exception {
        // given
        String nickname = "testNickname";
        String username = "testuser";
        String password = "password123";
        StoreUserRequest request = new StoreUserRequest(
            nickname,
            username,
            password
        );

        // when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 처리되었습니다."))
            .andExpect(jsonPath("$.payload.username").value(username))
            .andExpect(jsonPath("$.payload.nickname").value(nickname))
            .andExpect(jsonPath("$.payload.id").exists());

        // 데이터베이스 검증
        User savedUser = userRepository.findByUsername(username);
        assertThat(savedUser.getNickname()).isEqualTo(nickname);
        assertThat(passwordEncoder.matches(password, savedUser.getPassword())).isTrue();
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 username")
    void store_fail_duplicateUsername() throws Exception {
        // given
        String username = "testuser";
        User existingUser = new User(username, "encodedPassword", "existingNickname");
        userRepository.save(existingUser);

        StoreUserRequest request = new StoreUserRequest(
            "newNickname",
            username,
            "password123"
        );

        // when & then
        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("로그인 성공 - 유효한 사용자 정보로 로그인")
    void login_success() throws Exception {
        // given
        String username = "testuser";
        String password = "password123";
        User existingUser = new User(username, passwordEncoder.encode(password), "");
        userRepository.save(existingUser);

        LoginRequest request = new LoginRequest(
            username,
            password
        );

        // when & then
        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 처리되었습니다."))
            .andExpect(jsonPath("$.payload.id").value(existingUser.getId()))
            .andExpect(jsonPath("$.payload.username").value(username));

    }

    @Test
    @DisplayName("로그인 성공 - 유효한 사용자 정보로 로그인")
    void login_fail_invalidPassword() throws Exception {
        // given
        String username = "testuser";
        String password = "invalid";
        User existingUser = new User(username, "raw_password", "");
        userRepository.save(existingUser);

        LoginRequest request = new LoginRequest(
            username,
            password
        );

        // when & then
        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().is4xxClientError());

    }

}
