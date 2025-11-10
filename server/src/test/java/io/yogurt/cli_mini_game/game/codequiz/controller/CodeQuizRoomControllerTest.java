package io.yogurt.cli_mini_game.game.codequiz.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CreateRoomRequest;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import io.yogurt.cli_mini_game.game.codequiz.manager.CodeQuizRoomManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CodeQuizRoomController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("CodeQuizRoomController REST API 테스트")
class CodeQuizRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CodeQuizRoomManager roomManager;

    @Test
    @DisplayName("GET /api/games/code-quiz/rooms - 대기 중인 방 목록 조회 성공")
    void getRooms_Success() throws Exception {
        // given
        CodeQuizRoom room1 = new CodeQuizRoom("room1", "코드퀴즈 방 1", 4, Language.JAVA);
        CodeQuizRoom room2 = new CodeQuizRoom("room2", "코드퀴즈 방 2", 2, Language.JAVA);

        given(roomManager.getWaitingRooms()).willReturn(java.util.List.of(room1, room2));

        // when & then
        mockMvc.perform(get("/api/games/code-quiz/rooms"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 처리되었습니다."))
            .andExpect(jsonPath("$.payload.rooms").isArray())
            .andExpect(jsonPath("$.payload.rooms.length()").value(2))
            .andExpect(jsonPath("$.payload.rooms[0].roomId").value("room1"))
            .andExpect(jsonPath("$.payload.rooms[0].roomName").value("코드퀴즈 방 1"))
            .andExpect(jsonPath("$.payload.rooms[0].maxPlayers").value(4))
            .andExpect(jsonPath("$.payload.rooms[0].currentPlayers").value(0))
            .andExpect(jsonPath("$.payload.rooms[0].status").value("WAITING"))
            .andExpect(jsonPath("$.payload.rooms[1].roomId").value("room2"))
            .andExpect(jsonPath("$.payload.rooms[1].roomName").value("코드퀴즈 방 2"));
    }

    @Test
    @DisplayName("GET /api/games/code-quiz/rooms - 빈 방 목록 조회")
    void getRooms_Empty() throws Exception {
        // given
        given(roomManager.getWaitingRooms()).willReturn(List.of());

        // when & then
        mockMvc.perform(get("/api/games/code-quiz/rooms"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 처리되었습니다."))
            .andExpect(jsonPath("$.payload.rooms").isArray())
            .andExpect(jsonPath("$.payload.rooms.length()").value(0));
    }

    @Test
    @DisplayName("POST /api/games/code-quiz/rooms - 방 생성 성공")
    void createRoom_Success() throws Exception {
        // given
        CreateRoomRequest request = new CreateRoomRequest("새로운 방", 4, Language.JAVA);
        CodeQuizRoom createdRoom = new CodeQuizRoom("created-room-id", "새로운 방", 4, Language.JAVA);

        given(roomManager.createRoom(anyString(), anyInt(), any()))
            .willReturn(createdRoom);

        // when & then
        mockMvc.perform(post("/api/games/code-quiz/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("요청이 처리되었습니다."))
            .andExpect(jsonPath("$.payload.roomId").value("created-room-id"))
            .andExpect(jsonPath("$.payload.roomName").value("새로운 방"))
            .andExpect(jsonPath("$.payload.maxPlayers").value(4))
            .andExpect(jsonPath("$.payload.status").value("WAITING"));
    }

    @Test
    @DisplayName("POST /api/games/code-quiz/rooms - 잘못된 요청 (방 이름 누락)")
    void createRoom_InvalidRequest_EmptyRoomName() throws Exception {
        // given
        String invalidRequest = "{\"roomName\":\"\",\"maxPlayers\":4,\"language\":\"JAVA\"}";

        // when & then
        mockMvc.perform(post("/api/games/code-quiz/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/games/code-quiz/rooms - 잘못된 요청 (maxPlayers < 2)")
    void createRoom_InvalidRequest_TooFewPlayers() throws Exception {
        // given - maxPlayers < 2
        String invalidRequest = "{\"roomName\":\"테스트\",\"maxPlayers\":1,\"language\":\"JAVA\"}";

        // when & then
        mockMvc.perform(post("/api/games/code-quiz/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidRequest))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }
}
