package io.yogurt.cli_mini_game.client.ui;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialog;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import io.yogurt.cli_mini_game.client.handler.GameWebSocketClient;
import io.yogurt.cli_mini_game.client.service.HttpClientService;
import io.yogurt.cli_mini_game.client.util.UserSession;
import io.yogurt.cli_mini_game.common.game.dto.GameTypeDTO;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomDTO;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.JoinRoomRequest;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.StartGameRequest;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.SubmitAnswerRequest;
import io.yogurt.cli_mini_game.common.user.dto.UserInfoResponse;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Lanterna GUI 기반 TUI 콘솔 인터페이스
 */
public class ConsoleUI {

    private final HttpClientService httpClient;
    private final UserSession userSession;
    private final ObjectMapper objectMapper;
    private Screen screen;
    private MultiWindowTextGUI textGUI;
    private ScreenManager screenManager;
    private GameWebSocketClient webSocketClient;
    private GameScreen gameScreen;
    private String currentRoomId;

    public ConsoleUI(String serverUrl) {
        this.httpClient = new HttpClientService(serverUrl);
        this.userSession = UserSession.getInstance();
        this.objectMapper = new ObjectMapper();
    }

    public void start() {
        try {
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            screen = terminalFactory.createScreen();
            screen.startScreen();

            // 레트로 터미널 스타일 테마 설정
            textGUI = new MultiWindowTextGUI(
                screen,
                new DefaultWindowManager(),
                new EmptySpace(TerminalTheme.Colors.BACKGROUND)
            );

            while (true) {
                if (!userSession.isLoggedIn()) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (screen != null) {
                    screen.stopScreen();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showLoginMenu() {
        BasicWindow window = new BasicWindow("╔═══ CODE QUIZ TERMINAL ═══╗");
        Panel panel = new Panel(new GridLayout(1));

        // 타이틀 라벨
        Label titleLabel = new Label(">>> AUTHENTICATION REQUIRED <<<");
        titleLabel.setForegroundColor(TerminalTheme.Colors.TITLE);
        panel.addComponent(titleLabel);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("[ LOGIN ]", this::login));
        panel.addComponent(new Button("[ REGISTER ]", this::register));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("[ EXIT ]", () -> {
            try {
                screen.stopScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }));

        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }

    private void login() {
        String username = TextInputDialog.showDialog(textGUI, ">>> LOGIN", "USERNAME:", "");
        if (username == null || username.trim().isEmpty()) return;

        String password = TextInputDialog.showDialog(textGUI, ">>> LOGIN", "PASSWORD:", "");
        if (password == null || password.trim().isEmpty()) return;

        try {
            UserInfoResponse userInfo = httpClient.login(username.trim(), password.trim());
            userSession.login(userInfo);
            MessageDialog.showMessageDialog(textGUI, "[SUCCESS]",
                "LOGIN SUCCESSFUL!\nWELCOME, " + userInfo.nickname() + "!",
                MessageDialogButton.OK);
        } catch (Exception e) {
            MessageDialog.showMessageDialog(textGUI, "[ERROR]",
                "LOGIN FAILED:\n" + e.getMessage(),
                MessageDialogButton.OK);
        }
    }

    private void register() {
        String username = TextInputDialog.showDialog(textGUI, ">>> REGISTER", "USERNAME:", "");
        if (username == null || username.trim().isEmpty()) return;

        String password = TextInputDialog.showDialog(textGUI, ">>> REGISTER", "PASSWORD:", "");
        if (password == null || password.trim().isEmpty()) return;

        String nickname = TextInputDialog.showDialog(textGUI, ">>> REGISTER", "NICKNAME:", "");
        if (nickname == null || nickname.trim().isEmpty()) return;

        try {
            UserInfoResponse userInfo = httpClient.register(username.trim(), password.trim(), nickname.trim());
            userSession.login(userInfo);
            MessageDialog.showMessageDialog(textGUI, "[SUCCESS]",
                "REGISTRATION SUCCESSFUL!\nAUTO-LOGIN COMPLETE.\nWELCOME, " + userInfo.nickname() + "!",
                MessageDialogButton.OK);
        } catch (Exception e) {
            MessageDialog.showMessageDialog(textGUI, "[ERROR]",
                "REGISTRATION FAILED:\n" + e.getMessage(),
                MessageDialogButton.OK);
        }
    }

    private void showMainMenu() {
        BasicWindow window = new BasicWindow("╔═══ MAIN MENU [" + userSession.getNickname() + "] ═══╗");
        Panel panel = new Panel(new GridLayout(1));

        Label statusLabel = new Label(">>> SYSTEM READY <<<");
        statusLabel.setForegroundColor(TerminalTheme.Colors.SUCCESS);
        panel.addComponent(statusLabel);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("[ SELECT GAME ]", this::selectGame));
        panel.addComponent(new Button("[ LOGOUT ]", () -> {
            userSession.logout();
        }));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("[ EXIT ]", () -> {
            try {
                screen.stopScreen();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }));

        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }

    private void selectGame() {
        try {
            List<GameTypeDTO> gameTypes = httpClient.getGameTypes();

            BasicWindow window = new BasicWindow("╔═══ GAME SELECTION ═══╗");
            Panel panel = new Panel(new GridLayout(1));

            Label infoLabel = new Label(">>> AVAILABLE GAMES <<<");
            infoLabel.setForegroundColor(TerminalTheme.Colors.ACCENT);
            panel.addComponent(infoLabel);
            panel.addComponent(new EmptySpace());

            for (GameTypeDTO gameType : gameTypes) {
                panel.addComponent(new Button(
                    "[ " + gameType.name() + " ] - " + gameType.description(),
                    () -> {
                        if (gameType.code().equals("CODE_QUIZ")) {
                            showCodeQuizMenu();
                        }
                    }
                ));
            }

            panel.addComponent(new EmptySpace());
            panel.addComponent(new Button("[ BACK ]", window::close));

            window.setComponent(panel);
            textGUI.addWindowAndWait(window);
        } catch (Exception e) {
            MessageDialog.showMessageDialog(textGUI, "[ERROR]",
                "FAILED TO LOAD GAME LIST:\n" + e.getMessage(),
                MessageDialogButton.OK);
        }
    }

    private void showCodeQuizMenu() {
        BasicWindow window = new BasicWindow("╔═══ CODE QUIZ ═══╗");
        Panel panel = new Panel(new GridLayout(1));

        Label titleLabel = new Label(">>> SELECT MODE <<<");
        titleLabel.setForegroundColor(TerminalTheme.Colors.TITLE);
        panel.addComponent(titleLabel);
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("[ FIND ROOM ]", () -> {
            findRoom();
            window.close();
        }));
        panel.addComponent(new Button("[ CREATE ROOM ]", () -> {
            createRoom();
            window.close();
        }));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("[ BACK ]", window::close));

        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }

    private void findRoom() {
        try {
            List<CodeQuizRoomDTO> rooms = httpClient.getCodeQuizRooms();

            if (rooms.isEmpty()) {
                MessageDialog.showMessageDialog(textGUI, "[INFO]",
                    "NO AVAILABLE ROOMS.\nCREATE A NEW ROOM TO START!",
                    MessageDialogButton.OK);
                return;
            }

            BasicWindow window = new BasicWindow("╔═══ ROOM LIST ═══╗");
            Panel panel = new Panel(new GridLayout(1));

            Label infoLabel = new Label(">>> AVAILABLE ROOMS <<<");
            infoLabel.setForegroundColor(TerminalTheme.Colors.ACCENT);
            panel.addComponent(infoLabel);
            panel.addComponent(new EmptySpace());

            for (CodeQuizRoomDTO room : rooms) {
                String roomInfo = String.format("[ %s ] [%d/%d]",
                    room.roomName(), room.currentPlayers(), room.maxPlayers());
                panel.addComponent(new Button(roomInfo, () -> {
                    joinRoom(room.roomId());
                    window.close();
                }));
            }

            panel.addComponent(new EmptySpace());
            panel.addComponent(new Button("[ BACK ]", window::close));

            window.setComponent(panel);
            textGUI.addWindowAndWait(window);
        } catch (Exception e) {
            MessageDialog.showMessageDialog(textGUI, "[ERROR]",
                "FAILED TO LOAD ROOM LIST:\n" + e.getMessage(),
                MessageDialogButton.OK);
        }
    }

    private void createRoom() {
        String roomName = TextInputDialog.showDialog(textGUI, ">>> CREATE ROOM", "ROOM NAME:", "");
        if (roomName == null || roomName.trim().isEmpty()) return;

        String maxPlayersStr = TextInputDialog.showDialog(textGUI, ">>> CREATE ROOM", "MAX PLAYERS (2-4):", "2");
        if (maxPlayersStr == null) return;

        try {
            int maxPlayers = Integer.parseInt(maxPlayersStr.trim());
            if (maxPlayers < 2 || maxPlayers > 4) {
                MessageDialog.showMessageDialog(textGUI, "[ERROR]",
                    "MAX PLAYERS MUST BE BETWEEN 2-4.",
                    MessageDialogButton.OK);
                return;
            }

            // 언어 선택
            BasicWindow langWindow = new BasicWindow("╔═══ SELECT LANGUAGE ═══╗");
            Panel langPanel = new Panel(new GridLayout(1));

            Label langLabel = new Label(">>> CHOOSE PROGRAMMING LANGUAGE <<<");
            langLabel.setForegroundColor(TerminalTheme.Colors.ACCENT);
            langPanel.addComponent(langLabel);
            langPanel.addComponent(new EmptySpace());

            AtomicBoolean selected = new AtomicBoolean(false);
            Language[] selectedLang = new Language[1];

            langPanel.addComponent(new Button("[ JAVA ]", () -> {
                selectedLang[0] = Language.JAVA;
                selected.set(true);
                langWindow.close();
            }));
            langPanel.addComponent(new Button("[ PYTHON ]", () -> {
                selectedLang[0] = Language.PYTHON;
                selected.set(true);
                langWindow.close();
            }));

            langWindow.setComponent(langPanel);
            textGUI.addWindowAndWait(langWindow);

            if (!selected.get()) return;

            CodeQuizRoomDTO room = httpClient.createCodeQuizRoom(roomName.trim(), maxPlayers, selectedLang[0]);
            MessageDialog.showMessageDialog(textGUI, "[SUCCESS]",
                "ROOM CREATED SUCCESSFULLY!\nROOM: " + room.roomName(),
                MessageDialogButton.OK);

            joinRoom(room.roomId());

        } catch (NumberFormatException e) {
            MessageDialog.showMessageDialog(textGUI, "[ERROR]",
                "INVALID NUMBER FORMAT.",
                MessageDialogButton.OK);
        } catch (Exception e) {
            MessageDialog.showMessageDialog(textGUI, "[ERROR]",
                "ROOM CREATION FAILED:\n" + e.getMessage(),
                MessageDialogButton.OK);
        }
    }

    private void joinRoom(String roomId) {
        try {
            // Screen 모드로 전환
            screen.stopScreen();

            // GameScreen 시작
            screenManager = new ScreenManager();
            screenManager.initialize();
            gameScreen = new GameScreen(screenManager);
            gameScreen.startAutoRefresh();

            currentRoomId = roomId;

            // WebSocket 연결
            String wsUrl = "ws://localhost:8080/game-websocket";
            webSocketClient = new GameWebSocketClient(new URI(wsUrl), gameScreen);
            webSocketClient.connectBlocking();
            Thread.sleep(500);

            // 방 구독 및 입장
            webSocketClient.subscribe("/topic/code-quiz/room/" + roomId, "room-" + roomId);
            JoinRoomRequest joinRequest = new JoinRoomRequest(roomId, userSession.getNickname());
            String json = objectMapper.writeValueAsString(joinRequest);
            webSocketClient.sendMessage("/app/code-quiz/join", json);

            // 게임 루프
            handleGameLoop();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (gameScreen != null) {
                gameScreen.stopAutoRefresh();
                gameScreen.reset();
            }
            if (screenManager != null) {
                try {
                    screenManager.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // GUI 모드로 복귀
            try {
                screen.startScreen();
                textGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TerminalTheme.Colors.BACKGROUND));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleGameLoop() throws IOException {
        while (webSocketClient.isConnected()) {
            KeyStroke keyStroke = screenManager.getTerminal().pollInput();

            if (keyStroke == null) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }

            Character character = keyStroke.getCharacter();

            // ESC 키로 나가기
            if (keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape) {
                leaveRoom();
                break;
            }

            // 게임 진행 중이 아닐 때 명령어 처리
            if (!gameScreen.isGameInProgress()) {
                if (character != null && character == 's') {
                    startGame();
                } else if (character != null && character == 'q') {
                    leaveRoom();
                    break;
                }
            }
        }
    }

    private void startGame() {
        try {
            StartGameRequest startRequest = new StartGameRequest(currentRoomId);
            String json = objectMapper.writeValueAsString(startRequest);
            webSocketClient.sendMessage("/app/code-quiz/start", json);
        } catch (Exception e) {
            // 에러 무시
        }
    }

    private void leaveRoom() {
        if (webSocketClient != null && webSocketClient.isConnected()) {
            webSocketClient.disconnect();
        }
        currentRoomId = null;
    }
}
