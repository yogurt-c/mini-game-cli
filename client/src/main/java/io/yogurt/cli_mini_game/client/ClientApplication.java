package io.yogurt.cli_mini_game.client;

import io.yogurt.cli_mini_game.client.ui.ConsoleUI;

/**
 * 클라이언트 메인 애플리케이션
 */
public class ClientApplication {

    public static void main(String[] args) {
        // 서버 URL (기본값: localhost:8080)
        String serverUrl = "http://localhost:8080";

        // 명령행 인자로 서버 URL 지정 가능
        if (args.length > 0) {
            serverUrl = args[0];
        }

        ConsoleUI consoleUI = new ConsoleUI(serverUrl);
        consoleUI.start();
    }
}
