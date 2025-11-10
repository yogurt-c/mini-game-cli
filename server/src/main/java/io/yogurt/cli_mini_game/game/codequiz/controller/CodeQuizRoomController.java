package io.yogurt.cli_mini_game.game.codequiz.controller;

import io.yogurt.cli_mini_game.common.api.ApiResponse;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomDTO;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomListResponse;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CreateRoomRequest;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizPlayer;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import io.yogurt.cli_mini_game.game.codequiz.manager.CodeQuizRoomManager;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 코드퀴즈 방 관리 REST API
 */
@RestController
@RequestMapping("/api/games/code-quiz/rooms")
@RequiredArgsConstructor
@Slf4j
public class CodeQuizRoomController {

    private final CodeQuizRoomManager roomManager;

    /**
     * 대기 중인 방 목록 조회 (초기 진입 시 사용)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<CodeQuizRoomListResponse>> getRooms() {
        log.debug("GET /api/games/code-quiz/rooms - Fetching waiting rooms");

        List<CodeQuizRoom> rooms = roomManager.getWaitingRooms();
        CodeQuizRoomListResponse response = new CodeQuizRoomListResponse(
            rooms.stream()
                .map(this::toDTO)
                .collect(Collectors.toList())
        );

        log.debug("Found {} waiting rooms", rooms.size());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    /**
     * 방 생성 (REST API) Note: WebSocket을 통한 방 생성도 가능하지만, 초기 생성은 REST API 사용
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CodeQuizRoomDTO>> createRoom(
        @RequestBody CreateRoomRequest request) {
        log.debug("POST /api/games/code-quiz/rooms - Creating room: {} (language: {})",
            request.roomName(), request.language());

        CodeQuizRoom room = roomManager.createRoom(
            request.roomName(),
            request.maxPlayers(),
            request.language()
        );

        log.info("Room created: {} (id: {}, language: {})",
            room.getRoomName(), room.getRoomId(), room.getLanguage());
        return ResponseEntity.ok(ApiResponse.ok(toDTO(room)));
    }

    private CodeQuizRoomDTO toDTO(CodeQuizRoom room) {
        List<String> playerNicknames = room.getPlayers().stream()
            .map(CodeQuizPlayer::getNickname)
            .collect(Collectors.toList());

        return new CodeQuizRoomDTO(
            room.getRoomId(),
            room.getRoomName(),
            room.getStatus().name(),
            room.getMaxPlayers(),
            room.getCurrentPlayerCount(),
            playerNicknames,
            room.getCreatedAt()
        );
    }
}
