package io.yogurt.cli_mini_game.client.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.yogurt.cli_mini_game.client.ui.GameScreen;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * WebSocket STOMP 클라이언트
 */
public class GameWebSocketClient extends WebSocketClient {

    private final ObjectMapper objectMapper;
    private final GameScreen gameScreen;
    private String sessionId;
    private boolean connected = false;

    public GameWebSocketClient(URI serverUri, GameScreen gameScreen) {
        super(serverUri);
        this.objectMapper = new ObjectMapper();
        this.gameScreen = gameScreen;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("\n[연결됨] 게임 서버에 연결되었습니다.");
        connected = true;

        // STOMP CONNECT 프레임 전송
        String connectFrame = "CONNECT\n" +
                "accept-version:1.1,1.2\n" +
                "heart-beat:10000,10000\n" +
                "\n" +
                "\u0000";
        send(connectFrame);
    }

    @Override
    public void onMessage(String message) {
        try {
            // STOMP 프레임 파싱
            if (message.startsWith("CONNECTED")) {
                handleConnected(message);
            } else if (message.startsWith("MESSAGE")) {
                handleMessage(message);
            } else if (message.startsWith("ERROR")) {
                System.err.println("\n[오류] " + message);
            }
        } catch (Exception e) {
            System.err.println("\n[메시지 처리 오류] " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("\n[연결 종료] " + reason);
        connected = false;
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("\n[WebSocket 오류] " + ex.getMessage());
        ex.printStackTrace();
    }

    private void handleConnected(String message) {
        // CONNECTED 프레임에서 session ID 추출
        String[] lines = message.split("\n");
        for (String line : lines) {
            if (line.startsWith("session:")) {
                sessionId = line.substring("session:".length());
                break;
            }
        }
        System.out.println("[인증됨] 세션 ID: " + sessionId);
    }

    private void handleMessage(String stompFrame) throws JsonProcessingException {
        // STOMP MESSAGE 프레임에서 본문 추출
        String[] parts = stompFrame.split("\n\n", 2);
        if (parts.length < 2) {
            return;
        }

        String body = parts[1].replace("\u0000", "");
        JsonNode jsonNode = objectMapper.readTree(body);

        String messageType = jsonNode.has("messageType") ? jsonNode.get("messageType").asText() : "";

        // 메시지 타입에 따라 처리
        switch (messageType) {
            case "PLAYER_JOINED":
                gameScreen.handlePlayerJoined(body);
                break;
            case "GAME_START":
                gameScreen.handleGameStart(body);
                break;
            case "QUESTION":
                gameScreen.handleQuestion(body);
                break;
            case "ANSWER_RESULT":
                gameScreen.handleAnswerResult(body);
                break;
            case "GAME_OVER":
                gameScreen.handleGameOver(body);
                break;
            default:
                // 기타 메시지는 무시
        }
    }

    /**
     * STOMP SUBSCRIBE 프레임 전송
     */
    public void subscribe(String destination, String subscriptionId) {
        String subscribeFrame = "SUBSCRIBE\n" +
                "id:" + subscriptionId + "\n" +
                "destination:" + destination + "\n" +
                "\n" +
                "\u0000";
        send(subscribeFrame);
        System.out.println("[구독] " + destination);
    }

    /**
     * STOMP SEND 프레임 전송
     */
    public void sendMessage(String destination, String jsonBody) {
        String sendFrame = "SEND\n" +
                "destination:" + destination + "\n" +
                "content-type:application/json\n" +
                "\n" +
                jsonBody + "\u0000";
        send(sendFrame);
    }

    /**
     * STOMP DISCONNECT 프레임 전송
     */
    public void disconnect() {
        String disconnectFrame = "DISCONNECT\n\n\u0000";
        send(disconnectFrame);
        close();
    }

    public String getSessionId() {
        return sessionId;
    }

    public boolean isConnected() {
        return connected && !isClosed();
    }
}
