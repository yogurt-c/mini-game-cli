package io.yogurt.cli_mini_game.game.codequiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.AnswerResultMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameOverMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.QuestionMessage;
import io.yogurt.cli_mini_game.game.codequiz.component.CodeQuizRoomBroadcaster;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizPlayer;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import io.yogurt.cli_mini_game.game.codequiz.entity.CodeQuestion;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CodeQuizGameServiceTest {

    @Mock
    private CodeQuestionService questionService;

    @Mock
    private CodeQuizRoomBroadcaster broadcaster;

    @InjectMocks
    private CodeQuizGameService gameService;

    private CodeQuizRoom room;
    private CodeQuizPlayer player1;
    private CodeQuizPlayer player2;
    private CodeQuestion sampleQuestion;

    @BeforeEach
    void setUp() {
        room = new CodeQuizRoom("room1", "Test Room", 2, Language.JAVA);
        player1 = new CodeQuizPlayer("session1", "Player1");
        player2 = new CodeQuizPlayer("session2", "Player2");
        room.addPlayer(player1);
        room.addPlayer(player2);

        sampleQuestion = new CodeQuestion(
            "int x = 5;\nSystem.out.println(x)",
            "2",
            "Missing semicolon",
            Language.JAVA
        );
        sampleQuestion.setId(1L);
    }

    @Test
    @DisplayName("게임 시작 - 성공")
    void startGame_Success() {
        // given
        when(questionService.getRandomQuestionByLanguage(Language.JAVA)).thenReturn(sampleQuestion);

        // when
        QuestionMessage result = gameService.startGame(room);

        // then
        assertThat(result).isNotNull();
        assertThat(result.messageType()).isEqualTo("QUESTION");
        assertThat(result.questionNumber()).isEqualTo(1);

        // GameStartMessage와 QuestionMessage가 브로드캐스트되었는지 확인
        verify(broadcaster, times(2)).broadcastToRoom(eq("room1"), any());
    }

    @Test
    @DisplayName("다음 문제 제시")
    void nextQuestion() {
        // given
        room.startGame();
        when(questionService.getRandomQuestionByLanguage(Language.JAVA)).thenReturn(sampleQuestion);

        // when
        QuestionMessage result = gameService.nextQuestion(room);

        // then
        assertThat(result).isNotNull();
        assertThat(result.questionNumber()).isEqualTo(1);
        assertThat(result.code()).isEqualTo(sampleQuestion.getCode());
        verify(broadcaster).broadcastToRoom(eq("room1"), any(QuestionMessage.class));
    }

    @Test
    @DisplayName("정답 제출 - 정답이고 내 장애물이 있는 경우")
    void submitAnswer_Correct_WithObstacles() {
        // given
        room.startGame();
        room.setCurrentQuestion(1L);
        player1.addObstacle();
        player1.addObstacle();

        when(questionService.getQuestionById(1L)).thenReturn(sampleQuestion);
        when(questionService.getRandomQuestionByLanguage(Language.JAVA)).thenReturn(sampleQuestion);
        when(questionService.checkAnswer(any(), any())).thenReturn(true);

        // when
        AnswerResultMessage result = gameService.submitAnswer(room, "session1", Arrays.asList(2));

        // then
        assertThat(result.correct()).isTrue();
        assertThat(result.action()).isEqualTo("REMOVE_OBSTACLE");
        assertThat(player1.getObstacleCount()).isEqualTo(1);
        assertThat(player1.getScore()).isEqualTo(1);
        verify(broadcaster).broadcastToRoom(eq("room1"), any(AnswerResultMessage.class));
    }

    @Test
    @DisplayName("정답 제출 - 정답이고 장애물이 없는 경우 (상대에게 전송)")
    void submitAnswer_Correct_WithoutObstacles() {
        // given
        room.startGame();
        room.setCurrentQuestion(1L);

        when(questionService.getQuestionById(1L)).thenReturn(sampleQuestion);
        when(questionService.getRandomQuestionByLanguage(Language.JAVA)).thenReturn(sampleQuestion);
        when(questionService.checkAnswer(any(), any())).thenReturn(true);

        // when
        AnswerResultMessage result = gameService.submitAnswer(room, "session1", Arrays.asList(2));

        // then
        assertThat(result.correct()).isTrue();
        assertThat(result.action()).isEqualTo("SEND_OBSTACLE");
        assertThat(result.targetPlayer()).isEqualTo("Player2");
        assertThat(player1.getObstacleCount()).isEqualTo(0);
        assertThat(player2.getObstacleCount()).isEqualTo(1);
        assertThat(player1.getScore()).isEqualTo(1);
    }

    @Test
    @DisplayName("정답 제출 - 오답인 경우")
    void submitAnswer_Wrong() {
        // given
        room.startGame();
        room.setCurrentQuestion(1L);

        when(questionService.getQuestionById(1L)).thenReturn(sampleQuestion);
        when(questionService.checkAnswer(any(), any())).thenReturn(false);

        // when
        AnswerResultMessage result = gameService.submitAnswer(room, "session1",
            Arrays.asList(1, 2));

        // then
        assertThat(result.correct()).isFalse();
        assertThat(result.action()).isEqualTo("NONE");
        assertThat(player1.getScore()).isEqualTo(0);
        assertThat(player1.getObstacleCount()).isEqualTo(0);
        assertThat(player2.getObstacleCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("정답 제출 - 상대 플레이어 패배 처리")
    void submitAnswer_OpponentDefeated() {
        // given
        room.startGame();
        room.setCurrentQuestion(1L);

        // 상대 플레이어에게 이미 4개의 장애물이 있음
        player2.addObstacle();
        player2.addObstacle();
        player2.addObstacle();
        player2.addObstacle();

        when(questionService.getQuestionById(1L)).thenReturn(sampleQuestion);
        when(questionService.checkAnswer(any(), any())).thenReturn(true);

        // when
        AnswerResultMessage result = gameService.submitAnswer(room, "session1", Arrays.asList(2));

        // then
        assertThat(result.correct()).isTrue();
        assertThat(player2.getObstacleCount()).isEqualTo(5);
        assertThat(player2.isAlive()).isFalse();

        // 게임 종료 메시지도 전송되었는지 확인
        verify(broadcaster, times(2)).broadcastToRoom(eq("room1"), any());
    }

    @Test
    @DisplayName("죽은 플레이어는 답안 제출 불가")
    void submitAnswer_DeadPlayer() {
        // given
        room.startGame();
        player1.setDead();

        // when & then
        assertThatThrownBy(() -> gameService.submitAnswer(room, "session1", Arrays.asList(2)))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Dead player cannot submit answer");
    }

    @Test
    @DisplayName("존재하지 않는 플레이어 답안 제출")
    void submitAnswer_PlayerNotFound() {
        // given
        room.startGame();

        // when & then
        assertThatThrownBy(() -> gameService.submitAnswer(room, "unknown", Arrays.asList(2)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Player not found in room");
    }

    @Test
    @DisplayName("게임 종료 - 플레이어 패배")
    void endGame_PlayerDefeated() {
        // given
        room.startGame();
        player2.setDead();

        // when
        gameService.endGame(room);

        // then
        verify(broadcaster).broadcastToRoom(eq("room1"), any(GameOverMessage.class));
    }
}
