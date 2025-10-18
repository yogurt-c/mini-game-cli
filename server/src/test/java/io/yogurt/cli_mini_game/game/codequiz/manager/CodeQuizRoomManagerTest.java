package io.yogurt.cli_mini_game.game.codequiz.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.game.codequiz.component.CodeQuizRoomBroadcaster;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizPlayer;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import io.yogurt.cli_mini_game.game.codequiz.domain.RoomStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CodeQuizRoomManager 테스트")
class CodeQuizRoomManagerTest {

    @Mock
    private CodeQuizRoomBroadcaster broadcaster;

    @InjectMocks
    private CodeQuizRoomManager roomManager;


    @Test
    @DisplayName("방 생성 성공")
    void createRoom_Success() {
        // given
        String roomName = "테스트 방";
        int maxPlayers = 4;
        Language language = Language.JAVA;

        // when
        CodeQuizRoom room = roomManager.createRoom(roomName, maxPlayers, language);

        // then
        assertThat(room).isNotNull();
        assertThat(room.getRoomName()).isEqualTo(roomName);
        assertThat(room.getMaxPlayers()).isEqualTo(maxPlayers);
        assertThat(room.getLanguage()).isEqualTo(language);
        assertThat(room.getStatus()).isEqualTo(RoomStatus.WAITING);
        assertThat(room.getCurrentPlayerCount()).isEqualTo(0);

        // 브로드캐스트 호출 확인
        verify(broadcaster, times(1)).broadcastRoomListUpdate(any());
    }

    @Test
    @DisplayName("대기 중인 방 목록 조회")
    void getWaitingRooms_Success() {
        // given
        CodeQuizRoom room1 = roomManager.createRoom("방1", 4, Language.JAVA);
        CodeQuizRoom room2 = roomManager.createRoom("방2", 2, Language.JAVA);
        CodeQuizRoom room3 = roomManager.createRoom("방3", 4, Language.JAVA);

        // room3을 게임 중으로 변경하기 위해 플레이어 2명 추가
        roomManager.joinRoom(room3.getRoomId(), "session1", "플레이어1");
        roomManager.joinRoom(room3.getRoomId(), "session2", "플레이어2");
        roomManager.startGame(room3.getRoomId());

        // when
        List<CodeQuizRoom> waitingRooms = roomManager.getWaitingRooms();

        // then
        assertThat(waitingRooms).hasSize(2);
        assertThat(waitingRooms).extracting(CodeQuizRoom::getRoomName)
            .containsExactlyInAnyOrder("방1", "방2");
    }

    @Test
    @DisplayName("방이 꽉 찬 경우 대기 목록에서 제외")
    void getWaitingRooms_ExcludeFullRooms() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 2, Language.JAVA);

        // 방을 가득 채움
        roomManager.joinRoom(room.getRoomId(), "session1", "플레이어1");
        roomManager.joinRoom(room.getRoomId(), "session2", "플레이어2");

        // when
        List<CodeQuizRoom> waitingRooms = roomManager.getWaitingRooms();

        // then
        assertThat(waitingRooms).isEmpty();
    }

    @Test
    @DisplayName("특정 방 조회 성공")
    void getRoom_Success() {
        // given
        CodeQuizRoom createdRoom = roomManager.createRoom("테스트 방", 4, Language.JAVA);

        // when
        CodeQuizRoom foundRoom = roomManager.getRoom(createdRoom.getRoomId());

        // then
        assertThat(foundRoom).isNotNull();
        assertThat(foundRoom.getRoomId()).isEqualTo(createdRoom.getRoomId());
        assertThat(foundRoom.getRoomName()).isEqualTo("테스트 방");
    }

    @Test
    @DisplayName("존재하지 않는 방 조회 시 null 반환")
    void getRoom_NotFound() {
        // when
        CodeQuizRoom room = roomManager.getRoom("non-existent-id");

        // then
        assertThat(room).isNull();
    }

    @Test
    @DisplayName("플레이어 방 입장 성공")
    void joinRoom_Success() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 4, Language.JAVA);
        String sessionId = "session123";
        String nickname = "테스터";

        // when
        roomManager.joinRoom(room.getRoomId(), sessionId, nickname);

        // then
        CodeQuizRoom updatedRoom = roomManager.getRoom(room.getRoomId());
        assertThat(updatedRoom.getCurrentPlayerCount()).isEqualTo(1);
        assertThat(updatedRoom.getPlayers()).hasSize(1);
        assertThat(updatedRoom.getPlayers().get(0).getNickname()).isEqualTo(nickname);

        // 플레이어 조회 확인
        CodeQuizPlayer player = roomManager.getPlayer(sessionId);
        assertThat(player).isNotNull();
        assertThat(player.getNickname()).isEqualTo(nickname);

        // 브로드캐스트 호출 확인
        // - createRoom: broadcastRoomListUpdate 1회
        // - joinRoom: broadcastRoomUpdate 1회 + broadcastRoomListUpdate 1회
        // 총 broadcastRoomListUpdate 2회, broadcastRoomUpdate 1회
        verify(broadcaster, times(1)).broadcastRoomUpdate(any());
        verify(broadcaster, times(2)).broadcastRoomListUpdate(any());
    }

    @Test
    @DisplayName("존재하지 않는 방 입장 시도 시 예외 발생")
    void joinRoom_RoomNotFound() {
        // when & then
        assertThatThrownBy(() ->
            roomManager.joinRoom("non-existent-id", "session123", "테스터")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Room not found");
    }

    @Test
    @DisplayName("플레이어 방 퇴장 성공 - 빈 방은 자동 삭제되므로 검증 방식 변경")
    void leaveRoom_Success() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 4, Language.JAVA);
        String sessionId = "session123";
        roomManager.joinRoom(room.getRoomId(), sessionId, "테스터");

        // when
        roomManager.leaveRoom(sessionId);

        // then - 방이 비어서 삭제되므로 null 확인
        CodeQuizRoom updatedRoom = roomManager.getRoom(room.getRoomId());
        assertThat(updatedRoom).isNull();

        CodeQuizPlayer player = roomManager.getPlayer(sessionId);
        assertThat(player).isNull();
    }

    @Test
    @DisplayName("방이 비면 자동 삭제")
    void leaveRoom_EmptyRoomAutoRemove() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 4, Language.JAVA);
        String sessionId = "session123";
        roomManager.joinRoom(room.getRoomId(), sessionId, "테스터");

        // when
        roomManager.leaveRoom(sessionId);

        // then
        CodeQuizRoom deletedRoom = roomManager.getRoom(room.getRoomId());
        assertThat(deletedRoom).isNull();
    }

    @Test
    @DisplayName("게임 중인 방은 비어도 삭제되지 않음")
    void leaveRoom_InGameRoomNotRemoved() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 4, Language.JAVA);
        String sessionId1 = "session1";
        String sessionId2 = "session2";
        roomManager.joinRoom(room.getRoomId(), sessionId1, "플레이어1");
        roomManager.joinRoom(room.getRoomId(), sessionId2, "플레이어2");

        // 게임 시작
        roomManager.startGame(room.getRoomId());

        // when - 모든 플레이어 퇴장
        roomManager.leaveRoom(sessionId1);
        roomManager.leaveRoom(sessionId2);

        // then - 게임 중인 방은 삭제되지 않음
        CodeQuizRoom updatedRoom = roomManager.getRoom(room.getRoomId());
        assertThat(updatedRoom).isNotNull();
        assertThat(updatedRoom.getStatus()).isEqualTo(RoomStatus.IN_GAME);
    }

    @Test
    @DisplayName("게임 시작 성공")
    void startGame_Success() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 4, Language.JAVA);
        roomManager.joinRoom(room.getRoomId(), "session1", "플레이어1");
        roomManager.joinRoom(room.getRoomId(), "session2", "플레이어2");

        // when
        roomManager.startGame(room.getRoomId());

        // then
        CodeQuizRoom updatedRoom = roomManager.getRoom(room.getRoomId());
        assertThat(updatedRoom.getStatus()).isEqualTo(RoomStatus.IN_GAME);

        // 게임 중인 방은 대기 목록에서 제외
        List<CodeQuizRoom> waitingRooms = roomManager.getWaitingRooms();
        assertThat(waitingRooms).isEmpty();
    }

    @Test
    @DisplayName("플레이어가 2명 미만일 때 게임 시작 시 예외 발생")
    void startGame_NotEnoughPlayers() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 4, Language.JAVA);
        roomManager.joinRoom(room.getRoomId(), "session1", "플레이어1");

        // when & then
        assertThatThrownBy(() ->
            roomManager.startGame(room.getRoomId())
        )
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Need at least 2 players");
    }

    @Test
    @DisplayName("존재하지 않는 방에서 게임 시작 시도 시 예외 발생")
    void startGame_RoomNotFound() {
        // when & then
        assertThatThrownBy(() ->
            roomManager.startGame("non-existent-id")
        )
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Room not found");
    }

    @Test
    @DisplayName("방 삭제 성공")
    void removeRoom_Success() {
        // given
        CodeQuizRoom room = roomManager.createRoom("테스트 방", 4, Language.JAVA);
        roomManager.joinRoom(room.getRoomId(), "session1", "플레이어1");

        // when
        roomManager.removeRoom(room.getRoomId());

        // then
        CodeQuizRoom deletedRoom = roomManager.getRoom(room.getRoomId());
        assertThat(deletedRoom).isNull();

        // 방에 있던 플레이어도 삭제됨
        CodeQuizPlayer player = roomManager.getPlayer("session1");
        assertThat(player).isNull();
    }

    @Test
    @DisplayName("플레이어가 다른 방에 입장 시 기존 방에서 자동 퇴장")
    void joinRoom_AutoLeaveFromPreviousRoom() {
        // given
        CodeQuizRoom room1 = roomManager.createRoom("방1", 4, Language.JAVA);
        CodeQuizRoom room2 = roomManager.createRoom("방2", 4, Language.JAVA);
        String sessionId = "session123";

        // when
        roomManager.joinRoom(room1.getRoomId(), sessionId, "테스터");
        roomManager.joinRoom(room2.getRoomId(), sessionId, "테스터");

        // then
        CodeQuizRoom updatedRoom1 = roomManager.getRoom(room1.getRoomId());
        CodeQuizRoom updatedRoom2 = roomManager.getRoom(room2.getRoomId());

        assertThat(updatedRoom1.getCurrentPlayerCount()).isEqualTo(0);
        assertThat(updatedRoom2.getCurrentPlayerCount()).isEqualTo(1);
    }
}
