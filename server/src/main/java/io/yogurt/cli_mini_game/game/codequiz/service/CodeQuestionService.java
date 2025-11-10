package io.yogurt.cli_mini_game.game.codequiz.service;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.game.codequiz.entity.CodeQuestion;
import io.yogurt.cli_mini_game.game.codequiz.repository.CodeQuestionRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CodeQuestionService {

    private final CodeQuestionRepository codeQuestionRepository;

    /**
     * 랜덤으로 문제 1개 가져오기
     */
    public CodeQuestion getRandomQuestion() {

        return codeQuestionRepository.findRandomQuestion()
            .orElseThrow(() -> new IllegalStateException("No questions available"));
    }

    /**
     * 특정 언어의 랜덤 문제 가져오기
     */
    public CodeQuestion getRandomQuestionByLanguage(Language language) {

        return codeQuestionRepository.findRandomQuestionByLanguage(language)
            .orElseThrow(() -> new IllegalStateException(
                "No questions available for language: " + language));
    }

    /**
     * 답안 검증
     *
     * @param question 문제
     * @param answer   플레이어가 제출한 틀린 라인 번호들
     * @return 정답 여부
     */
    public boolean checkAnswer(CodeQuestion question, List<Integer> answer) {

        if (answer == null || answer.isEmpty()) {

            return false;
        }

        List<Integer> correctAnswer = question.getIncorrectLineNumbers();

        // 정렬해서 비교 (순서 무관)
        List<Integer> sortedAnswer = answer.stream().sorted().toList();
        List<Integer> sortedCorrect = correctAnswer.stream().sorted().toList();

        return sortedAnswer.equals(sortedCorrect);
    }

    /**
     * ID로 특정 문제 조회
     */
    public CodeQuestion getQuestionById(Long questionId) {
        return codeQuestionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalStateException("Question not found: " + questionId));
    }

    /**
     * 전체 문제 개수 조회
     */
    public long getTotalQuestionCount() {

        return codeQuestionRepository.count();
    }
}
