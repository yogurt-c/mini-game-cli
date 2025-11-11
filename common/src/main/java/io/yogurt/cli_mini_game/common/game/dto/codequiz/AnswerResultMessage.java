package io.yogurt.cli_mini_game.common.game.dto.codequiz;

import java.util.Map;
import java.util.Optional;

/**
 * 답안 결과 메시지 (서버 → 클라이언트)
 */
public record AnswerResultMessage(
    String messageType,           // "ANSWER_RESULT"
    String answerer,              // 답안 제출자 닉네임
    boolean correct,              // 정답 여부
    String action,                // "REMOVE_OBSTACLE", "SEND_OBSTACLE", "NONE"
    String targetPlayer,          // 장애물을 받는 플레이어 (SEND_OBSTACLE일 때만 존재)
    Map<String, PlayerStatus> playerStatuses  // 모든 플레이어의 상태
) {
    public AnswerResultMessage(
        String answerer,
        boolean correct,
        String action,
        String targetPlayer,
        Map<String, PlayerStatus> playerStatuses
    ) {
        this("ANSWER_RESULT", answerer, correct, action, targetPlayer, playerStatuses);
    }

    /**
     * targetPlayer를 Optional로 반환하는 헬퍼 메서드
     * action이 "SEND_OBSTACLE"일 때만 값이 존재
     */
    public Optional<String> getTargetPlayer() {
        return Optional.ofNullable(targetPlayer);
    }

    public record PlayerStatus(
        String nickname,
        int obstacleCount,
        int score,
        boolean alive
    ) {}
}
