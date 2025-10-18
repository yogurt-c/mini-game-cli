package io.yogurt.cli_mini_game.game.codequiz.domain;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.Getter;

/**
 * In-Memory 코드퀴즈 방 정보
 */
@Getter
public class CodeQuizRoom {

    private final String roomId;

    private final String roomName;

    private final int maxPlayers;

    private final Language language;  // 게임 언어 (JAVA, PYTHON)

    private RoomStatus status;

    private final List<CodeQuizPlayer> players;

    private final LocalDateTime createdAt;

    // 게임 로직 관련 필드
    private Long currentQuestionId;           // 현재 문제 ID
    private int currentQuestionNumber;        // 현재 문제 번호 (1부터 시작)
    private LocalDateTime gameStartTime;      // 게임 시작 시간
    private static final int GAME_TIME_LIMIT_MINUTES = 5;  // 게임 시간 제한 (5분)
    private static final int MAX_OBSTACLE_HEIGHT = 5;      // 패배 기준 장애물 높이

    public CodeQuizRoom(String roomId, String roomName, int maxPlayers, Language language) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.maxPlayers = maxPlayers;
        this.language = language;
        this.status = RoomStatus.WAITING;
        this.players = new CopyOnWriteArrayList<>();
        this.createdAt = LocalDateTime.now();
    }

    public boolean isFull() {

        return players.size() >= maxPlayers;
    }

    public boolean canJoin() {

        return status == RoomStatus.WAITING && !isFull();
    }

    public void addPlayer(CodeQuizPlayer player) {

        if (!canJoin()) {

            throw new IllegalStateException("Cannot join this room");
        }

        players.add(player);
    }

    public void removePlayer(String sessionId) {

        players.removeIf(p -> p.getSessionId().equals(sessionId));
    }

    public CodeQuizPlayer getPlayer(String sessionId) {

        return players.stream()
            .filter(p -> p.getSessionId().equals(sessionId))
            .findFirst()
            .orElse(null);
    }

    public int getCurrentPlayerCount() {

        return players.size();
    }

    public void startGame() {

        if (players.size() < 2) {

            throw new IllegalStateException("Need at least 2 players to start");
        }

        this.status = RoomStatus.IN_GAME;
        this.gameStartTime = LocalDateTime.now();
        this.currentQuestionNumber = 0;
    }

    public void finishGame() {

        this.status = RoomStatus.FINISHED;
    }

    public boolean isEmpty() {

        return players.isEmpty();
    }

    /**
     * 방장인지 확인 (첫 번째 플레이어가 방장)
     */
    public boolean isHost(CodeQuizPlayer player) {
        if (players.isEmpty()) {
            return false;
        }
        return players.get(0).getSessionId().equals(player.getSessionId());
    }

    /**
     * 현재 문제 설정
     */
    public void setCurrentQuestion(Long questionId) {
        this.currentQuestionId = questionId;
        this.currentQuestionNumber++;
    }

    /**
     * 게임 시간 초과 여부 확인
     */
    public boolean isTimeOut() {
        if (gameStartTime == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(gameStartTime.plusMinutes(GAME_TIME_LIMIT_MINUTES));
    }

    /**
     * 게임 종료 조건 확인 (시간 초과 또는 플레이어 패배)
     */
    public boolean shouldEndGame() {
        if (isTimeOut()) {
            return true;
        }

        // 살아있는 플레이어가 1명 이하면 게임 종료
        long alivePlayers = players.stream()
            .filter(CodeQuizPlayer::isAlive)
            .count();

        return alivePlayers <= 1;
    }

    /**
     * 승자 결정
     */
    public CodeQuizPlayer getWinner() {
        List<CodeQuizPlayer> alivePlayers = players.stream()
            .filter(CodeQuizPlayer::isAlive)
            .toList();

        if (alivePlayers.size() == 1) {
            return alivePlayers.get(0);
        }

        // 시간 초과 시: 점수가 높은 플레이어 승리
        return players.stream()
            .max((p1, p2) -> Integer.compare(p1.getScore(), p2.getScore()))
            .orElse(null);
    }

    public int getMaxObstacleHeight() {
        return MAX_OBSTACLE_HEIGHT;
    }
}
