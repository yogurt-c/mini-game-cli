package io.yogurt.cli_mini_game.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.AnswerResultMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameOverMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.GameStartMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.PlayerJoinedMessage;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.QuestionMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 게임 화면을 실시간으로 새로고침하는 TUI
 */
public class GameScreen {

    private final ScreenManager screenManager;
    private final ObjectMapper objectMapper;
    private final ScheduledExecutorService refreshExecutor;

    // 게임 상태
    private volatile boolean gameInProgress = false;
    private volatile boolean waitingForNextQuestion = false;
    private volatile String currentRoom = "";
    private volatile List<String> currentPlayers = new ArrayList<>();
    private volatile int maxPlayers = 2;
    private volatile QuestionMessage currentQuestion = null;
    private volatile Map<String, AnswerResultMessage.PlayerStatus> playerStatuses = null;
    private volatile String lastAnswerer = "";
    private volatile String lastAction = "";
    private volatile boolean lastCorrect = false;
    private volatile GameOverMessage gameOverMessage = null;
    private volatile List<String> statusLog = new ArrayList<>();

    public GameScreen(ScreenManager screenManager) {
        this.screenManager = screenManager;
        this.objectMapper = new ObjectMapper();
        this.refreshExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * 화면 자동 새로고침 시작
     */
    public void startAutoRefresh() {
        refreshExecutor.scheduleAtFixedRate(() -> {
            try {
                render();
            } catch (Exception e) {
                // 에러 무시 (화면 새로고침 중 에러 발생 시)
            }
        }, 0, 200, TimeUnit.MILLISECONDS); // 200ms마다 새로고침
    }

    /**
     * 화면 자동 새로고침 정지
     */
    public void stopAutoRefresh() {
        refreshExecutor.shutdown();
    }

    /**
     * 메인 렌더링 함수
     */
    public void render() throws IOException {
        screenManager.clear();

        TerminalSize termSize = screenManager.getTerminalSize();
        int width = termSize.getColumns();
        int height = termSize.getRows();

        // 헤더
        renderHeader(width);

        if (gameOverMessage != null) {
            // 게임 종료 화면
            renderGameOver(width, height);
        } else if (gameInProgress) {
            // 게임 진행 중 화면
            renderGameInProgress(width, height);
        } else {
            // 대기실 화면
            renderWaitingRoom(width, height);
        }

        screenManager.refresh();
    }

    private void renderHeader(int width) {
        // 레트로 터미널 스타일 헤더
        screenManager.drawDivider(0, 0, width, ScreenManager.DividerStyle.HEAVY);

        String title = "▶ CODE QUIZ TERMINAL v1.0 ◀";
        int startCol = (width - title.length()) / 2;
        screenManager.drawText(startCol, 0, title, TerminalTheme.Colors.TITLE, TerminalTheme.Colors.BACKGROUND);

        // 시스템 정보 (레트로 스타일)
        String sysInfo = "[SYSTEM ACTIVE]";
        screenManager.drawText(2, 0, sysInfo, TerminalTheme.Colors.SUCCESS, TerminalTheme.Colors.BACKGROUND);
    }

    private void renderWaitingRoom(int width, int height) {
        int startRow = 2;

        // WAITING 배너
        String[] bannerLines = TerminalTheme.Banners.WAITING.split("\n");
        for (int i = 0; i < bannerLines.length; i++) {
            int col = (width - bannerLines[i].length()) / 2;
            screenManager.drawText(col, startRow + i, bannerLines[i],
                TerminalTheme.Colors.ACCENT, TerminalTheme.Colors.BACKGROUND);
        }

        int contentStart = startRow + bannerLines.length + 1;

        // 방 정보 - 레트로 터미널 스타일
        screenManager.drawBox(2, contentStart, width - 4, 6, "ROOM INFO");
        screenManager.drawText(4, contentStart + 1, ">> ROOM NAME: " + currentRoom,
            TerminalTheme.Colors.FOREGROUND, TerminalTheme.Colors.BACKGROUND);
        screenManager.drawText(4, contentStart + 2, ">> CAPACITY: " + currentPlayers.size() + "/" + maxPlayers,
            TerminalTheme.Colors.ACCENT, TerminalTheme.Colors.BACKGROUND);

        String status = currentPlayers.size() >= 2 ? "[READY TO START]" : "[WAITING...]";
        TextColor statusColor = currentPlayers.size() >= 2 ? TerminalTheme.Colors.SUCCESS : TerminalTheme.Colors.WARNING;
        screenManager.drawText(4, contentStart + 3, ">> STATUS: " + status,
            statusColor, TerminalTheme.Colors.BACKGROUND);

        // 플레이어 목록
        int playerListStart = contentStart + 7;
        screenManager.drawBox(2, playerListStart, width - 4, maxPlayers + 3, "CONNECTED PLAYERS");
        for (int i = 0; i < currentPlayers.size(); i++) {
            screenManager.drawText(4, playerListStart + 1 + i,
                TerminalTheme.Symbols.ARROW_RIGHT + " " + currentPlayers.get(i),
                TerminalTheme.Colors.SUCCESS, TerminalTheme.Colors.BACKGROUND);
        }
        // 빈 슬롯
        for (int i = currentPlayers.size(); i < maxPlayers; i++) {
            screenManager.drawText(4, playerListStart + 1 + i,
                TerminalTheme.Symbols.HOLLOW_BULLET + " [EMPTY SLOT]",
                TerminalTheme.Colors.DIM_TEXT, TerminalTheme.Colors.BACKGROUND);
        }

        // 상태 로그
        renderStatusLog(2, playerListStart + maxPlayers + 4, width - 4, height - (playerListStart + maxPlayers + 7));

        // 도움말 - 레트로 스타일
        int helpRow = height - 2;
        screenManager.drawDivider(0, helpRow - 1, width, ScreenManager.DividerStyle.DASHED);
        screenManager.drawText(2, helpRow, "[COMMANDS] <S> START GAME  |  <ESC> EXIT",
            TerminalTheme.Colors.WARNING, TerminalTheme.Colors.BACKGROUND);
    }

    private void renderGameInProgress(int width, int height) {
        int leftCol = 2;
        int rightCol = width / 2 + 2;
        int middleWidth = width / 2 - 4;

        // 왼쪽: 문제 영역 - 레트로 터미널 스타일
        if (currentQuestion != null) {
            int questionHeight = Math.min(25, height - 10);
            screenManager.drawBox(leftCol, 2, middleWidth, questionHeight,
                "QUESTION #" + currentQuestion.questionNumber());

            String[] codeLines = currentQuestion.code().split("\n");
            for (int i = 0; i < codeLines.length && i < questionHeight - 3; i++) {
                String lineNum = String.format("%02d", i + 1);
                String code = codeLines[i];
                if (code.length() > middleWidth - 8) {
                    code = code.substring(0, middleWidth - 8);
                }

                // 라인 번호 - 시안색
                screenManager.drawText(leftCol + 2, 3 + i, lineNum,
                    TerminalTheme.Colors.ACCENT, TerminalTheme.Colors.BACKGROUND);
                // 구분자
                screenManager.drawText(leftCol + 4, 3 + i, " │ ",
                    TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
                // 코드 - 녹색 (레트로 모니터 스타일)
                screenManager.drawText(leftCol + 7, 3 + i, code,
                    TerminalTheme.Colors.FOREGROUND, TerminalTheme.Colors.BACKGROUND);
            }

            // 입력 안내 - 레트로 스타일
            int inputRow = 2 + questionHeight + 1;
            screenManager.drawText(leftCol, inputRow, TerminalTheme.Symbols.ARROW_RIGHT + " INPUT ERROR LINE NUMBER (COMMA SEPARATED):",
                TerminalTheme.Colors.WARNING, TerminalTheme.Colors.BACKGROUND);
        }

        // 오른쪽: 플레이어 상태
        renderPlayerStatuses(rightCol, 2, middleWidth);

        // 마지막 액션 정보 - 레트로 스타일
        if (!lastAnswerer.isEmpty()) {
            int actionRow = height - 8;
            screenManager.drawBox(rightCol, actionRow, middleWidth, 6, "LAST ACTION");

            screenManager.drawText(rightCol + 2, actionRow + 1,
                ">> PLAYER: " + lastAnswerer,
                TerminalTheme.Colors.ACCENT, TerminalTheme.Colors.BACKGROUND);

            String resultIcon = lastCorrect ? TerminalTheme.Symbols.CHECK : TerminalTheme.Symbols.CROSS;
            String resultText = lastCorrect ? "CORRECT!" : "WRONG!";
            TextColor resultColor = lastCorrect ? TerminalTheme.Colors.SUCCESS : TerminalTheme.Colors.ERROR;

            screenManager.drawText(rightCol + 2, actionRow + 2,
                ">> RESULT: " + resultIcon + " " + resultText,
                resultColor, TerminalTheme.Colors.BACKGROUND);

            screenManager.drawText(rightCol + 2, actionRow + 3,
                ">> ACTION: " + lastAction,
                TerminalTheme.Colors.WARNING, TerminalTheme.Colors.BACKGROUND);
        }

        // 상태 로그
        renderStatusLog(leftCol, height - 5, width - 4, 3);
    }

    private void renderPlayerStatuses(int startCol, int startRow, int width) {
        if (playerStatuses == null || playerStatuses.isEmpty()) {
            return;
        }

        int boxHeight = playerStatuses.size() * 5 + 2;
        screenManager.drawBox(startCol, startRow, width, boxHeight, "PLAYER STATUS");

        int row = startRow + 1;
        for (Map.Entry<String, AnswerResultMessage.PlayerStatus> entry : playerStatuses.entrySet()) {
            String nickname = entry.getKey();
            AnswerResultMessage.PlayerStatus status = entry.getValue();

            // 레트로 스타일 - 상태 표시
            TextColor color = status.alive() ? TerminalTheme.Colors.ALIVE : TerminalTheme.Colors.DEAD;
            String statusIcon = status.alive() ? TerminalTheme.Symbols.BULLET : TerminalTheme.Symbols.SKULL;
            String statusText = status.alive() ? "[ALIVE]" : "[DEAD]";

            screenManager.drawText(startCol + 2, row++,
                statusIcon + " " + nickname + " " + statusText,
                color, TerminalTheme.Colors.BACKGROUND);

            // 점수 표시 - 레트로 스타일
            String scoreDisplay = String.format("   SCORE: %03d PTS", status.score());
            screenManager.drawText(startCol + 2, row++, scoreDisplay,
                TerminalTheme.Colors.ACCENT, TerminalTheme.Colors.BACKGROUND);

            // 장애물 표시 - 레트로 프로그레스 바
            String bar = TerminalTheme.Symbols.PROGRESS_FULL.repeat(status.obstacleCount()) +
                        TerminalTheme.Symbols.PROGRESS_EMPTY.repeat(5 - status.obstacleCount());
            String obstacles = String.format("   OBSTACLES: [%s] %d/5", bar, status.obstacleCount());

            TextColor obstacleColor = status.obstacleCount() >= 4 ? TerminalTheme.Colors.ERROR :
                                     (status.obstacleCount() >= 2 ? TerminalTheme.Colors.WARNING :
                                     TerminalTheme.Colors.SUCCESS);

            screenManager.drawText(startCol + 2, row++, obstacles, obstacleColor, TerminalTheme.Colors.BACKGROUND);

            // 구분선
            if (row - startRow < boxHeight - 1) {
                screenManager.drawText(startCol + 2, row++,
                    TerminalTheme.Dividers.dashed(width - 4),
                    TerminalTheme.Colors.BORDER, TerminalTheme.Colors.BACKGROUND);
            }
            row++; // 빈 줄
        }
    }

    private void renderGameOver(int width, int height) {
        // GAME OVER ASCII 배너
        String[] bannerLines = TerminalTheme.Banners.GAME_OVER.split("\n");
        int bannerStart = 2;
        for (int i = 0; i < bannerLines.length; i++) {
            int col = (width - bannerLines[i].length()) / 2;
            screenManager.drawText(col, bannerStart + i, bannerLines[i],
                TerminalTheme.Colors.ERROR, TerminalTheme.Colors.BACKGROUND);
        }

        int contentStart = bannerStart + bannerLines.length + 2;
        int boxWidth = Math.min(70, width - 4);
        int boxHeight = 10 + gameOverMessage.playerStats().size() * 2;
        int startCol = (width - boxWidth) / 2;

        screenManager.drawBox(startCol, contentStart, boxWidth, boxHeight, "FINAL RESULTS");

        int row = contentStart + 2;

        // 종료 사유
        String reasonIcon = gameOverMessage.reason().equals("TIME_OUT") ? "⏱" : TerminalTheme.Symbols.SWORD;
        String reasonText = gameOverMessage.reason().equals("TIME_OUT") ? "TIME OUT" : "PLAYER DEFEATED";
        screenManager.drawText(startCol + 2, row++, ">> REASON: " + reasonIcon + " " + reasonText,
            TerminalTheme.Colors.WARNING, TerminalTheme.Colors.BACKGROUND);

        row++;

        // 승자
        screenManager.drawText(startCol + 2, row++, ">> WINNER: " + TerminalTheme.Symbols.CROWN + " " + gameOverMessage.winner(),
            TerminalTheme.Colors.SUCCESS, TerminalTheme.Colors.BACKGROUND);

        row++;
        screenManager.drawDivider(startCol + 2, row++, boxWidth - 4, ScreenManager.DividerStyle.HEAVY);
        row++;

        screenManager.drawText(startCol + 2, row++, "FINAL STANDINGS:",
            TerminalTheme.Colors.TITLE, TerminalTheme.Colors.BACKGROUND);

        // 플레이어 최종 통계
        for (Map.Entry<String, GameOverMessage.PlayerStats> entry : gameOverMessage.playerStats().entrySet()) {
            String nickname = entry.getKey();
            GameOverMessage.PlayerStats stats = entry.getValue();
            String statusIcon = stats.alive() ? TerminalTheme.Symbols.BULLET : TerminalTheme.Symbols.SKULL;
            TextColor statusColor = stats.alive() ? TerminalTheme.Colors.SUCCESS : TerminalTheme.Colors.ERROR;

            String statsLine = String.format("  %s %s │ SCORE: %03d │ OBSTACLES: %d/5",
                statusIcon, nickname, stats.finalScore(), stats.obstacleCount());

            screenManager.drawText(startCol + 2, row++, statsLine, statusColor, TerminalTheme.Colors.BACKGROUND);
        }

        row += 2;
        screenManager.drawDivider(startCol + 2, row++, boxWidth - 4, ScreenManager.DividerStyle.DASHED);
        screenManager.drawText(startCol + 2, row, "[PRESS ESC TO RETURN TO MAIN MENU]",
            TerminalTheme.Colors.WARNING, TerminalTheme.Colors.BACKGROUND);
    }

    private void renderStatusLog(int startCol, int startRow, int width, int height) {
        if (height <= 2) return;

        screenManager.drawBox(startCol, startRow, width, height, "SYSTEM LOG");

        int maxLines = height - 2;
        int startIndex = Math.max(0, statusLog.size() - maxLines);

        for (int i = startIndex; i < statusLog.size() && (i - startIndex) < maxLines; i++) {
            String logLine = "> " + statusLog.get(i);
            screenManager.drawText(startCol + 2, startRow + 1 + (i - startIndex), logLine,
                TerminalTheme.Colors.INFO, TerminalTheme.Colors.BACKGROUND);
        }
    }

    // 메시지 핸들러들
    public void handlePlayerJoined(String json) {
        try {
            PlayerJoinedMessage message = objectMapper.readValue(json, PlayerJoinedMessage.class);
            currentRoom = message.room().roomName();
            currentPlayers = new ArrayList<>(message.room().playerNicknames());
            maxPlayers = message.room().maxPlayers();
            addStatusLog(message.nickname() + " 님이 입장했습니다.");
        } catch (Exception e) {
            addStatusLog("플레이어 입장 메시지 오류: " + e.getMessage());
        }
    }

    public void handleGameStart(String json) {
        try {
            GameStartMessage message = objectMapper.readValue(json, GameStartMessage.class);
            gameInProgress = true;
            currentPlayers = new ArrayList<>(message.players());
            addStatusLog("게임이 시작되었습니다!");
        } catch (Exception e) {
            addStatusLog("게임 시작 메시지 오류: " + e.getMessage());
        }
    }

    public void handleQuestion(String json) {
        try {
            currentQuestion = objectMapper.readValue(json, QuestionMessage.class);
            waitingForNextQuestion = false;
            lastAnswerer = "";
            lastAction = "";
            addStatusLog("문제 #" + currentQuestion.questionNumber() + " 출제");
        } catch (Exception e) {
            addStatusLog("문제 메시지 오류: " + e.getMessage());
        }
    }

    public void handleAnswerResult(String json) {
        try {
            AnswerResultMessage message = objectMapper.readValue(json, AnswerResultMessage.class);
            waitingForNextQuestion = true;
            playerStatuses = message.playerStatuses();
            lastAnswerer = message.answerer();
            lastCorrect = message.correct();
            lastAction = getActionText(message.action(), message.targetPlayer());

            String resultText = message.answerer() + " - " + (message.correct() ? "정답" : "오답");
            addStatusLog(resultText);
        } catch (Exception e) {
            addStatusLog("답안 결과 메시지 오류: " + e.getMessage());
        }
    }

    public void handleGameOver(String json) {
        try {
            gameOverMessage = objectMapper.readValue(json, GameOverMessage.class);
            gameInProgress = false;
            addStatusLog("게임 종료! 승자: " + gameOverMessage.winner());
        } catch (Exception e) {
            addStatusLog("게임 종료 메시지 오류: " + e.getMessage());
        }
    }

    private String getActionText(String action, String targetPlayer) {
        return switch (action) {
            case "REMOVE_OBSTACLE" -> "자신의 장애물 제거";
            case "SEND_OBSTACLE" -> targetPlayer + "에게 장애물 전송";
            default -> "없음";
        };
    }

    private void addStatusLog(String message) {
        statusLog.add(message);
        if (statusLog.size() > 100) {
            statusLog.remove(0);
        }
    }

    // Getters
    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public boolean isWaitingForNextQuestion() {
        return waitingForNextQuestion;
    }

    public void setGameInProgress(boolean gameInProgress) {
        this.gameInProgress = gameInProgress;
    }

    public void reset() {
        gameInProgress = false;
        waitingForNextQuestion = false;
        currentRoom = "";
        currentPlayers.clear();
        currentQuestion = null;
        playerStatuses = null;
        lastAnswerer = "";
        lastAction = "";
        gameOverMessage = null;
        statusLog.clear();
    }
}
