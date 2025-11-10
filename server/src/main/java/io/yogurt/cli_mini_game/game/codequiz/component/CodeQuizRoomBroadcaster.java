package io.yogurt.cli_mini_game.game.codequiz.component;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomDTO;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomListResponse;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizPlayer;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * 코드퀴즈 방 목록 변경사항을 실시간으로 브로드캐스트
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CodeQuizRoomBroadcaster {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 방 목록이 변경될 때마다 구독 중인 모든 클라이언트에게 전송
     */
    public void broadcastRoomListUpdate(List<CodeQuizRoom> rooms) {
        log.debug("Broadcasting room list update: {} rooms", rooms.size());

        CodeQuizRoomListResponse response = new CodeQuizRoomListResponse(
            rooms.stream()
                .map(this::toDTO)
                .collect(Collectors.toList())
        );

        messagingTemplate.convertAndSend("/topic/code-quiz/rooms", response);
    }

    /**
     * 특정 방의 상태 변경을 해당 방 구독자들에게 전송
     */
    public void broadcastRoomUpdate(CodeQuizRoom room) {
        log.debug("Broadcasting room update for room: {}", room.getRoomId());

        CodeQuizRoomDTO roomDTO = toDTO(room);

        messagingTemplate.convertAndSend(
            "/topic/code-quiz/room/" + room.getRoomId(),
            roomDTO
        );
    }

    /**
     * 특정 방에 게임 메시지 브로드캐스트 (범용)
     */
    public void broadcastToRoom(String roomId, Object message) {
        log.debug("Broadcasting message to room: {}", roomId);

        messagingTemplate.convertAndSend(
            "/topic/code-quiz/room/" + roomId,
            message
        );
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
