package io.yogurt.cli_mini_game.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.AnswerResultMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameOverMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameStartMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.PlayerJoinedMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.QuestionMessage;

/**
 * 게임 진행 중 UI 처리
 */
public class GameUI {

    private final ObjectMapper objectMapper;
    private volatile boolean gameInProgress = false;
    private volatile boolean waitingForNextQuestion = false;

    public GameUI() {
        this.objectMapper = new ObjectMapper();
    }

    public void handlePlayerJoined(String json) {
        try {
            PlayerJoinedMessage message = objectMapper.readValue(json, PlayerJoinedMessage.class);
            System.out.println("\n[플레이어 입장] " + message.nickname() + " 님이 입장하셨습니다.");
            System.out.println("현재 플레이어 수: " + message.room().currentPlayers() + "/" + message.room().maxPlayers());

            // 플레이어 목록 출력
            if (!message.room().playerNicknames().isEmpty()) {
                System.out.println("대기 중인 플레이어:");
                message.room().playerNicknames().forEach(nickname ->
                        System.out.println("  - " + nickname));
            }
        } catch (Exception e) {
            System.err.println("플레이어 입장 메시지 처리 오류: " + e.getMessage());
        }
    }

    public void handleGameStart(String json) {
        try {
            GameStartMessage message = objectMapper.readValue(json, GameStartMessage.class);
            gameInProgress = true;

            System.out.println("\n" + "=".repeat(60));
            System.out.println("게임 시작!");
            System.out.println("=".repeat(60));
            System.out.println("참여 플레이어: " + String.join(", ", message.players()));
            System.out.println("게임 시간: " + message.gameDurationMinutes() + "분");
            System.out.println("=".repeat(60));
        } catch (Exception e) {
            System.err.println("게임 시작 메시지 처리 오류: " + e.getMessage());
        }
    }

    public void handleQuestion(String json) {
        try {
            QuestionMessage message = objectMapper.readValue(json, QuestionMessage.class);
            waitingForNextQuestion = false;

            System.out.println("\n" + "=".repeat(60));
            System.out.println("문제 #" + message.questionNumber());
            System.out.println("=".repeat(60));
            System.out.println("다음 코드에서 틀린 라인을 찾으세요:");
            System.out.println();

            // 코드를 라인 번호와 함께 출력
            String[] lines = message.code().split("\n");
            for (int i = 0; i < lines.length; i++) {
                System.out.printf("%2d | %s\n", i + 1, lines[i]);
            }

            System.out.println();
            System.out.println("=".repeat(60));
            System.out.print("틀린 라인 번호를 입력하세요 (여러 개는 쉼표로 구분): ");
        } catch (Exception e) {
            System.err.println("문제 메시지 처리 오류: " + e.getMessage());
        }
    }

    public void handleAnswerResult(String json) {
        try {
            AnswerResultMessage message = objectMapper.readValue(json, AnswerResultMessage.class);
            waitingForNextQuestion = true;

            System.out.println("\n" + "-".repeat(60));
            System.out.println("[답안 결과]");
            System.out.println("플레이어: " + message.answerer());
            System.out.println("정답 여부: " + (message.correct() ? "정답!" : "오답"));

            switch (message.action()) {
                case "REMOVE_OBSTACLE":
                    System.out.println("액션: 자신의 장애물 1개 제거");
                    break;
                case "SEND_OBSTACLE":
                    System.out.println("액션: " + message.targetPlayer() + "에게 장애물 전송");
                    break;
                case "NONE":
                    if (message.correct()) {
                        System.out.println("액션: 없음 (상대가 이미 패배)");
                    } else {
                        System.out.println("액션: 없음");
                    }
                    break;
            }

            // 현재 플레이어 상태 출력
            System.out.println("\n현재 상태:");
            message.playerStatuses().forEach((nickname, status) -> {
                String aliveStatus = status.alive() ? "생존" : "패배";
                System.out.printf("  %s: 점수=%d, 장애물=%d, 상태=%s\n",
                        nickname, status.score(), status.obstacleCount(), aliveStatus);
            });
            System.out.println("-".repeat(60));

            if (message.correct()) {
                System.out.println("\n다음 문제를 기다리는 중...");
            }
        } catch (Exception e) {
            System.err.println("답안 결과 메시지 처리 오류: " + e.getMessage());
        }
    }

    public void handleGameOver(String json) {
        try {
            GameOverMessage message = objectMapper.readValue(json, GameOverMessage.class);
            gameInProgress = false;

            System.out.println("\n" + "=".repeat(60));
            System.out.println("게임 종료!");
            System.out.println("=".repeat(60));

            String reasonText = message.reason().equals("TIME_OUT") ? "시간 초과" : "플레이어 패배";
            System.out.println("종료 사유: " + reasonText);
            System.out.println("승자: " + message.winner());

            System.out.println("\n최종 결과:");
            message.playerStats().forEach((nickname, stats) -> {
                String aliveStatus = stats.alive() ? "생존" : "패배";
                System.out.printf("  %s: 점수=%d, 장애물=%d, 상태=%s\n",
                        nickname, stats.finalScore(), stats.obstacleCount(), aliveStatus);
            });
            System.out.println("=".repeat(60));
            System.out.println("\n메인 메뉴로 돌아가려면 엔터를 누르세요...");
        } catch (Exception e) {
            System.err.println("게임 종료 메시지 처리 오류: " + e.getMessage());
        }
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public boolean isWaitingForNextQuestion() {
        return waitingForNextQuestion;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }
}
