package io.yogurt.cli_mini_game.game;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.yogurt.cli_mini_game.game.entity.GameType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class GameTypeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GameTypeRepository gameTypeRepository;

    @BeforeEach
    void setUp() {
        gameTypeRepository.deleteAll();
    }

    @Test
    @DisplayName("게임 종류 조회 성공 - 게임 종류 목록 반환")
    void getGameTypes_success() throws Exception {
        // given
        String code = "CODE_QUIZ";
        String name = "코드 퀴즈";
        String description = "코드의 틀린 라인을 찾아 상대방에게 장애물을 보내는 게임";
        GameType codeQuiz = new GameType(code, name, description);
        gameTypeRepository.save(codeQuiz);

        // when & then
        mockMvc.perform(get("/api/games/types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 처리되었습니다."))
            .andExpect(jsonPath("$.payload.gameTypes").isArray())
            .andExpect(jsonPath("$.payload.gameTypes.length()").value(1))
            .andExpect(jsonPath("$.payload.gameTypes[0].code").value(code))
            .andExpect(jsonPath("$.payload.gameTypes[0].name").value(name))
            .andExpect(jsonPath("$.payload.gameTypes[0].description").value(description));
    }

    @Test
    @DisplayName("게임 종류 조회 성공 - 게임이 없을 때 빈 배열 반환")
    void getGameTypes_emptyList() throws Exception {
        // given - setUp()에서 이미 모든 데이터 삭제됨

        // when & then
        mockMvc.perform(get("/api/games/types"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 처리되었습니다."))
            .andExpect(jsonPath("$.payload.gameTypes").isArray())
            .andExpect(jsonPath("$.payload.gameTypes.length()").value(0));
    }
}