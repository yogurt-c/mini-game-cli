package io.yogurt.cli_mini_game.game.codequiz.entity;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "code_questions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CodeQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false)
    private String incorrectLines;  // "3,5,7" 형식으로 저장

    @Column(columnDefinition = "TEXT")
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Language language;  // "JAVA", "PYTHON"

    public CodeQuestion(String code, String incorrectLines, String explanation, Language language) {
        this.code = code;
        this.incorrectLines = incorrectLines;
        this.explanation = explanation;
        this.language = language;
    }

    /**
     * 틀린 라인 번호들을 리스트로 반환
     */
    public java.util.List<Integer> getIncorrectLineNumbers() {
        return java.util.Arrays.stream(incorrectLines.split(","))
            .map(String::trim)
            .map(Integer::parseInt)
            .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 코드의 전체 라인 수
     */
    public int getTotalLines() {
        return (int) code.lines().count();
    }
}
