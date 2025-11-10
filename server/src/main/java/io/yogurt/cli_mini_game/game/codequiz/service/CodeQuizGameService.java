package io.yogurt.cli_mini_game.game.codequiz.service;

import io.yogurt.cli_mini_game.common.game.dto.codequiz.AnswerResultMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.AnswerResultMessage.PlayerStatus;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameOverMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameOverMessage.PlayerStats;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameStartMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.QuestionMessage;
import io.yogurt.cli_mini_game.game.codequiz.component.CodeQuizRoomBroadcaster;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizPlayer;
import io.yogurt.cli_mini_game.game.codequiz.domain.CodeQuizRoom;
import io.yogurt.cli_mini_game.game.codequiz.entity.CodeQuestion;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 코드퀴즈 게임 진행 로직
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CodeQuizGameService {

    private final CodeQuestionService questionService;
    private final CodeQuizRoomBroadcaster broadcaster;

    /**
     * 게임 시작 - 첫 문제 제시
     */
    public QuestionMessage startGame(CodeQuizRoom room) {
        log.info("Starting game in room: {}", room.getRoomId());

        // 게임 상태 업데이트
        room.startGame();

        // 게임 시작 메시지 브로드캐스트
        List<String> playerNames = room.getPlayers().stream()
            .map(CodeQuizPlayer::getNickname)
            .toList();

        GameStartMessage gameStartMsg = new GameStartMessage(playerNames, 5);
        broadcaster.broadcastToRoom(room.getRoomId(), gameStartMsg);

        // 첫 문제 제시
        return nextQuestion(room);
    }

    /**
     * 다음 문제 제시
     */
    public QuestionMessage nextQuestion(CodeQuizRoom room) {
        CodeQuestion question = questionService.getRandomQuestionByLanguage(room.getLanguage());
        room.setCurrentQuestion(question.getId());

        log.info("Question {} presented in room {} (language: {})",
            room.getCurrentQuestionNumber(), room.getRoomId(), room.getLanguage());

        QuestionMessage questionMsg = new QuestionMessage(
            room.getCurrentQuestionNumber(),
            question.getCode(),
            question.getTotalLines()
        );

        broadcaster.broadcastToRoom(room.getRoomId(), questionMsg);

        return questionMsg;
    }

    /**
     * 답안 제출 처리
     */
    public AnswerResultMessage submitAnswer(
        CodeQuizRoom room,
        String sessionId,
        List<Integer> answer
    ) {
        CodeQuizPlayer player = room.getPlayer(sessionId);
        if (player == null) {
            throw new IllegalArgumentException("Player not found in room");
        }

        if (!player.isAlive()) {
            throw new IllegalStateException("Dead player cannot submit answer");
        }

        // 현재 문제 가져오기
        CodeQuestion question = questionService.getQuestionById(room.getCurrentQuestionId());

        // 정답 검증
        boolean isCorrect = questionService.checkAnswer(question, answer);

        String action;
        String targetPlayer = null;

        if (isCorrect) {
            player.increaseScore();

            if (player.getObstacleCount() > 0) {
                // 내 장애물 제거
                player.removeObstacle();
                action = "REMOVE_OBSTACLE";
                log.info("Player {} answered correctly - removed obstacle", player.getNickname());
            } else {
                // 상대에게 장애물 전송
                CodeQuizPlayer opponent = findOpponent(room, player);
                if (opponent != null) {
                    opponent.addObstacle();
                    targetPlayer = opponent.getNickname();
                    action = "SEND_OBSTACLE";

                    // 상대가 패배했는지 확인
                    if (opponent.getObstacleCount() >= room.getMaxObstacleHeight()) {
                        opponent.setDead();
                        log.info("Player {} defeated by obstacles", opponent.getNickname());
                    }

                    log.info("Player {} answered correctly - sent obstacle to {}",
                        player.getNickname(), opponent.getNickname());
                } else {
                    action = "NONE";
                }
            }
        } else {
            action = "NONE";
            log.info("Player {} answered incorrectly", player.getNickname());
        }

        // 결과 메시지 생성
        Map<String, PlayerStatus> playerStatuses = buildPlayerStatuses(room);

        AnswerResultMessage resultMsg = new AnswerResultMessage(
            player.getNickname(),
            isCorrect,
            action,
            targetPlayer,
            playerStatuses
        );

        broadcaster.broadcastToRoom(room.getRoomId(), resultMsg);

        // 게임 종료 체크
        if (room.shouldEndGame()) {
            endGame(room);
        } else if (isCorrect) {
            // 정답이면 다음 문제
            nextQuestion(room);
        }

        return resultMsg;
    }

    /**
     * 게임 종료 처리
     */
    public void endGame(CodeQuizRoom room) {
        room.finishGame();

        String reason = room.isTimeOut() ? "TIME_OUT" : "PLAYER_DEFEATED";
        CodeQuizPlayer winner = room.getWinner();
        String winnerName = winner != null ? winner.getNickname() : "DRAW";

        Map<String, PlayerStats> stats = room.getPlayers().stream()
            .collect(Collectors.toMap(
                CodeQuizPlayer::getNickname,
                p -> new PlayerStats(
                    p.getNickname(),
                    p.getScore(),
                    p.getObstacleCount(),
                    p.isAlive()
                )
            ));

        GameOverMessage gameOverMsg = new GameOverMessage(reason, winnerName, stats);

        broadcaster.broadcastToRoom(room.getRoomId(), gameOverMsg);

        log.info("Game ended in room {}: winner={}, reason={}",
            room.getRoomId(), winnerName, reason);
    }

    /**
     * 상대 플레이어 찾기 (살아있는 플레이어 중)
     */
    private CodeQuizPlayer findOpponent(CodeQuizRoom room, CodeQuizPlayer currentPlayer) {
        return room.getPlayers().stream()
            .filter(p -> !p.getSessionId().equals(currentPlayer.getSessionId()))
            .filter(CodeQuizPlayer::isAlive)
            .findFirst()
            .orElse(null);
    }

    /**
     * 모든 플레이어 상태 정보 생성
     */
    private Map<String, PlayerStatus> buildPlayerStatuses(CodeQuizRoom room) {
        return room.getPlayers().stream()
            .collect(Collectors.toMap(
                CodeQuizPlayer::getNickname,
                p -> new PlayerStatus(
                    p.getNickname(),
                    p.getObstacleCount(),
                    p.getScore(),
                    p.isAlive()
                )
            ));
    }
}
