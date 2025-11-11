package io.yogurt.cli_mini_game.common.game.dto.codequiz;

import java.util.Map;

/**
 * 게임 종료 메시지 (서버 → 클라이언트)
 */
public record GameOverMessage(
    String messageType,           // "GAME_OVER"
    String reason,                // "PLAYER_DEFEATED", "TIME_OUT"
    String winner,                // 승자 닉네임
    Map<String, PlayerStats> playerStats  // 최종 통계
) {
    public GameOverMessage(
        String reason,
        String winner,
        Map<String, PlayerStats> playerStats
    ) {
        this("GAME_OVER", reason, winner, playerStats);
    }

    /**
     * 플레이어 최종 통계
     * 참고: nickname은 Map의 key와 중복되지만, 데이터 일관성을 위해 유지
     */
    public record PlayerStats(
        String nickname,
        int finalScore,
        int obstacleCount,
        boolean alive
    ) {}
}
