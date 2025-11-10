package io.yogurt.cli_mini_game.common.game.dto.codequiz;

import java.util.List;

/**
 * 답안 제출 요청 (클라이언트 → 서버)
 */
public record SubmitAnswerRequest(
    String roomId,
    List<Integer> lineNumbers  // 틀린 라인 번호들
) {
}
