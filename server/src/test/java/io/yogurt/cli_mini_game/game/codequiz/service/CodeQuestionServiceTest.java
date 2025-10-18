package io.yogurt.cli_mini_game.game.codequiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.game.codequiz.entity.CodeQuestion;
import io.yogurt.cli_mini_game.game.codequiz.repository.CodeQuestionRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeQuestionServiceTest {

    @Mock
    private CodeQuestionRepository codeQuestionRepository;

    @InjectMocks
    private CodeQuestionService codeQuestionService;

    private CodeQuestion sampleQuestion;

    @BeforeEach
    void setUp() {
        sampleQuestion = new CodeQuestion(
            "public class Test {\n    public static void main(String[] args) {\n        int x = 5\n        System.out.println(x);\n    }\n}",
            "3",
            "Missing semicolon on line 3",
            Language.JAVA
        );
        sampleQuestion.setId(1L);
    }

    @Test
    @DisplayName("랜덤 문제 조회 - 성공")
    void getRandomQuestion_Success() {
        // given
        when(codeQuestionRepository.findRandomQuestion()).thenReturn(Optional.of(sampleQuestion));

        // when
        CodeQuestion result = codeQuestionService.getRandomQuestion();

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getCode()).contains("int x = 5");
    }

    @Test
    @DisplayName("랜덤 문제 조회 - 문제가 없는 경우 예외 발생")
    void getRandomQuestion_NoQuestions() {
        // given
        when(codeQuestionRepository.findRandomQuestion()).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> codeQuestionService.getRandomQuestion())
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("No questions available");
    }

    @Test
    @DisplayName("정답 확인 - 정답인 경우")
    void checkAnswer_Correct() {
        // given
        List<Integer> answer = Arrays.asList(3);

        // when
        boolean result = codeQuestionService.checkAnswer(sampleQuestion, answer);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("정답 확인 - 오답인 경우")
    void checkAnswer_Wrong() {
        // given
        List<Integer> answer = Arrays.asList(2, 4);

        // when
        boolean result = codeQuestionService.checkAnswer(sampleQuestion, answer);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("정답 확인 - 순서가 다른 정답도 정답으로 처리")
    void checkAnswer_DifferentOrder() {
        // given
        CodeQuestion multiLineQuestion = new CodeQuestion(
            "Code with multiple errors",
            "3,5,7",
            "Multiple errors",
            Language.JAVA
        );
        List<Integer> answer = Arrays.asList(7, 3, 5);  // 순서가 다름

        // when
        boolean result = codeQuestionService.checkAnswer(multiLineQuestion, answer);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("정답 확인 - 개수가 다른 경우 오답 처리")
    void checkAnswer_WrongCount() {
        // given
        List<Integer> answer = Arrays.asList(3, 4, 5);  // 정답보다 많음

        // when
        boolean result = codeQuestionService.checkAnswer(sampleQuestion, answer);

        // then
        assertThat(result).isFalse();
    }
}
