package io.yogurt.cli_mini_game.game.codequiz.controller;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomDTO;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.JoinRoomRequest;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.PlayerJoinedMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.StartGameRequest;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.SubmitAnswerRequest;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizPlayer;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import io.yogurt.cli_mini_game.game.codequiz.manager.CodeQuizRoomManager;
import io.yogurt.cli_mini_game.game.codequiz.service.CodeQuizGameService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

/**
 * 코드퀴즈 게임 WebSocket Controller
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class CodeQuizGameController {

    private final CodeQuizRoomManager roomManager;
    private final CodeQuizGameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 방 입장
     * Client: SEND /app/code-quiz/join
     * Server: SEND /topic/code-quiz/room/{roomId} (방 전체에 브로드캐스트)
     */
    @MessageMapping("/code-quiz/join")
    public void joinRoom(@Payload JoinRoomRequest request,
                         SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Player joining room: {} (sessionId: {})", request.roomId(), sessionId);

        try {
            roomManager.joinRoom(request.roomId(), sessionId, request.nickname());

            CodeQuizRoom room = roomManager.getRoom(request.roomId());
            CodeQuizRoomDTO roomDTO = toDTO(room);

            // 방 전체에 브로드캐스트
            PlayerJoinedMessage message = new PlayerJoinedMessage(
                request.nickname(),
                roomDTO
            );

            messagingTemplate.convertAndSend(
                "/topic/code-quiz/room/" + request.roomId(),
                message
            );

            log.info("Player {} successfully joined room {}", request.nickname(), request.roomId());
        } catch (Exception e) {
            log.error("Failed to join room: {}", e.getMessage());
            // 에러 메시지를 개인에게만 전송
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                "Failed to join room: " + e.getMessage()
            );
        }
    }

    /**
     * 게임 시작
     * Client: SEND /app/code-quiz/start
     * Server: SEND /topic/code-quiz/room/{roomId} (GameStartMessage, QuestionMessage)
     */
    @MessageMapping("/code-quiz/start")
    public void startGame(@Payload StartGameRequest request,
                         SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Starting game in room: {} (requested by sessionId: {})", request.roomId(), sessionId);

        try {
            CodeQuizRoom room = roomManager.getRoom(request.roomId());
            if (room == null) {
                throw new IllegalArgumentException("Room not found: " + request.roomId());
            }

            // 방장만 게임 시작 가능
            CodeQuizPlayer requester = room.getPlayer(sessionId);
            if (requester == null || !room.isHost(requester)) {
                throw new IllegalStateException("Only the host can start the game");
            }

            // 게임 시작 (GameStartMessage와 첫 QuestionMessage 자동 브로드캐스트)
            gameService.startGame(room);

            log.info("Game started successfully in room: {}", request.roomId());
        } catch (Exception e) {
            log.error("Failed to start game: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                "Failed to start game: " + e.getMessage()
            );
        }
    }

    /**
     * 답안 제출
     * Client: SEND /app/code-quiz/answer
     * Server: SEND /topic/code-quiz/room/{roomId} (AnswerResultMessage, QuestionMessage or GameOverMessage)
     */
    @MessageMapping("/code-quiz/answer")
    public void submitAnswer(@Payload SubmitAnswerRequest request,
                            SimpMessageHeaderAccessor headerAccessor) {
        String sessionId = headerAccessor.getSessionId();
        log.info("Answer submitted in room: {} (sessionId: {}, answer: {})",
            request.roomId(), sessionId, request.lineNumbers());

        try {
            CodeQuizRoom room = roomManager.getRoom(request.roomId());
            if (room == null) {
                throw new IllegalArgumentException("Room not found: " + request.roomId());
            }

            // 답안 처리 (AnswerResultMessage 자동 브로드캐스트, 게임 종료 체크)
            gameService.submitAnswer(room, sessionId, request.lineNumbers());

            log.info("Answer processed successfully in room: {}", request.roomId());
        } catch (Exception e) {
            log.error("Failed to submit answer: {}", e.getMessage());
            messagingTemplate.convertAndSendToUser(
                sessionId,
                "/queue/errors",
                "Failed to submit answer: " + e.getMessage()
            );
        }
    }

    /**
     * WebSocket 연결 해제 시 처리
     */
    @EventListener
    public void handleWebSocketDisconnect(SessionDisconnectEvent event) {
        String sessionId = event.getSessionId();
        log.info("WebSocket disconnected: {}", sessionId);

        // 플레이어가 속한 방에서 퇴장 처리
        roomManager.leaveRoom(sessionId);
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
