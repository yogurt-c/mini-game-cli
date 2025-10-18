package io.yogurt.cli_mini_game.common.game.dto.codequiz;

/**
 * 문제 제시 메시지 (서버 → 클라이언트)
 */
public record QuestionMessage(
    String messageType,      // "QUESTION"
    int questionNumber,      // 문제 번호
    String code,             // 코드 전체
    int totalLines          // 전체 라인 수
) {
    public QuestionMessage(int questionNumber, String code, int totalLines) {
        this("QUESTION", questionNumber, code, totalLines);
    }
}
