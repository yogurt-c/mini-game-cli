package io.yogurt.cli_mini_game.game.codequiz.repository;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.game.codequiz.entity.CodeQuestion;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeQuestionRepository extends JpaRepository<CodeQuestion, Long> {

    /**
     * 랜덤으로 문제 1개 가져오기
     */
    @Query(value = "SELECT * FROM code_questions ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<CodeQuestion> findRandomQuestion();

    /**
     * 특정 언어의 랜덤 문제 1개 가져오기
     */
    @Query(value = "SELECT * FROM code_questions WHERE language = :language ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<CodeQuestion> findRandomQuestionByLanguage(@Param("language") Language language);

    /**
     * 전체 문제 개수
     */
    long count();
}
