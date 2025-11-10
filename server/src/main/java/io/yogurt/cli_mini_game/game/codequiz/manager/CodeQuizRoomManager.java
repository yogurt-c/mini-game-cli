package io.yogurt.cli_mini_game.game.codequiz.manager;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.game.codequiz.component.CodeQuizRoomBroadcaster;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizPlayer;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import io.yogurt.cli_mini_game.game.codequiz.domain.RoomStatus;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * In-Memory 코드퀴즈 방/플레이어 관리자
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CodeQuizRoomManager {

    private final Map<String, CodeQuizRoom> rooms = new ConcurrentHashMap<>();
    private final Map<String, CodeQuizPlayer> players = new ConcurrentHashMap<>();
    private final CodeQuizRoomBroadcaster broadcaster;

    /**
     * 방 생성
     */
    public CodeQuizRoom createRoom(String roomName, int maxPlayers, Language language) {
        String roomId = UUID.randomUUID().toString();
        CodeQuizRoom room = new CodeQuizRoom(roomId, roomName, maxPlayers, language);
        rooms.put(roomId, room);

        log.info("Room created: {} (id: {}, language: {})", roomName, roomId, language);

        // 방 목록 변경사항 브로드캐스트
        broadcaster.broadcastRoomListUpdate(getWaitingRooms());

        return room;
    }

    /**
     * 대기 중인 방 목록 조회 (입장 가능한 방만)
     */
    public List<CodeQuizRoom> getWaitingRooms() {
        return rooms.values().stream()
            .filter(room -> room.getStatus() == RoomStatus.WAITING)
            .filter(room -> !room.isFull())
            .collect(Collectors.toList());
    }

    /**
     * 특정 방 조회
     */
    public CodeQuizRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * 플레이어 방 입장
     */
    public void joinRoom(String roomId, String sessionId, String nickname) {
        CodeQuizRoom room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }

        // 기존 플레이어가 다른 방에 있다면 제거
        removePlayerFromAllRooms(sessionId);

        CodeQuizPlayer player = new CodeQuizPlayer(sessionId, nickname);
        room.addPlayer(player);
        players.put(sessionId, player);

        log.info("Player {} joined room: {}", nickname, roomId);

        // 방 상태 변경 브로드캐스트
        broadcaster.broadcastRoomUpdate(room);
        broadcaster.broadcastRoomListUpdate(getWaitingRooms());
    }

    /**
     * 플레이어 방 퇴장
     */
    public void leaveRoom(String sessionId) {
        CodeQuizPlayer player = players.remove(sessionId);
        if (player == null) {
            return;
        }

        // 플레이어가 속한 방 찾기
        CodeQuizRoom room = findRoomBySessionId(sessionId);
        if (room != null) {
            room.removePlayer(sessionId);
            log.info("Player {} left room: {}", player.getNickname(), room.getRoomId());

            // 방이 비었으면 삭제
            if (room.isEmpty() && room.getStatus() == RoomStatus.WAITING) {
                rooms.remove(room.getRoomId());
                log.info("Empty room removed: {}", room.getRoomId());
            } else {
                broadcaster.broadcastRoomUpdate(room);
            }

            broadcaster.broadcastRoomListUpdate(getWaitingRooms());
        }
    }

    /**
     * 플레이어가 속한 방 찾기
     */
    private CodeQuizRoom findRoomBySessionId(String sessionId) {
        return rooms.values().stream()
            .filter(room -> room.getPlayer(sessionId) != null)
            .findFirst()
            .orElse(null);
    }

    /**
     * 플레이어를 모든 방에서 제거
     */
    private void removePlayerFromAllRooms(String sessionId) {
        rooms.values().forEach(room -> room.removePlayer(sessionId));
    }

    /**
     * 방 삭제
     */
    public void removeRoom(String roomId) {
        CodeQuizRoom room = rooms.remove(roomId);
        if (room != null) {
            // 방에 있던 모든 플레이어 제거
            room.getPlayers().forEach(p -> players.remove(p.getSessionId()));
            log.info("Room removed: {}", roomId);

            broadcaster.broadcastRoomListUpdate(getWaitingRooms());
        }
    }

    /**
     * 게임 시작
     */
    public void startGame(String roomId) {
        CodeQuizRoom room = rooms.get(roomId);
        if (room == null) {
            throw new IllegalArgumentException("Room not found: " + roomId);
        }

        room.startGame();
        log.info("Game started in room: {}", roomId);

        broadcaster.broadcastRoomUpdate(room);
        broadcaster.broadcastRoomListUpdate(getWaitingRooms());
    }

    /**
     * 플레이어 조회
     */
    public CodeQuizPlayer getPlayer(String sessionId) {
        return players.get(sessionId);
    }
}
