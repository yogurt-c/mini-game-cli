package io.yogurt.cli_mini_game.client.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.yogurt.cli_mini_game.common.api.ApiResponse;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomDTO;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CodeQuizRoomListResponse;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.CreateRoomRequest;
import io.yogurt.cli_mini_game.common.game.dto.codequiz.Language;
import io.yogurt.cli_mini_game.common.game.dto.GameTypeDTO;
import io.yogurt.cli_mini_game.common.game.dto.GameTypeListResponse;
import io.yogurt.cli_mini_game.common.user.dto.LoginRequest;
import io.yogurt.cli_mini_game.common.user.dto.StoreUserRequest;
import io.yogurt.cli_mini_game.common.user.dto.UserInfoResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * REST API 호출을 위한 HTTP 클라이언트
 */
public class HttpClientService {

    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public HttpClientService(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * 회원 가입
     */
    public UserInfoResponse register(String username, String password, String nickname) throws IOException, InterruptedException {
        StoreUserRequest request = new StoreUserRequest(username, password, nickname);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ApiResponse<UserInfoResponse> apiResponse = objectMapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<UserInfoResponse>>() {}
            );
            return apiResponse.payload();
        } else {
            throw new IOException("Registration failed: " + response.body());
        }
    }

    /**
     * 로그인
     */
    public UserInfoResponse login(String username, String password) throws IOException, InterruptedException {
        LoginRequest request = new LoginRequest(username, password);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/user/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ApiResponse<UserInfoResponse> apiResponse = objectMapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<UserInfoResponse>>() {}
            );
            return apiResponse.payload();
        } else {
            throw new IOException("Login failed: " + response.body());
        }
    }

    /**
     * 게임 타입 목록 조회
     */
    public List<GameTypeDTO> getGameTypes() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/games/types"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ApiResponse<GameTypeListResponse> apiResponse = objectMapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<GameTypeListResponse>>() {}
            );
            return apiResponse.payload().gameTypes();
        } else {
            throw new IOException("Failed to get game types: " + response.body());
        }
    }

    /**
     * 코드 퀴즈 대기 중인 방 목록 조회
     */
    public List<CodeQuizRoomDTO> getCodeQuizRooms() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/games/code-quiz/rooms"))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ApiResponse<CodeQuizRoomListResponse> apiResponse = objectMapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<CodeQuizRoomListResponse>>() {}
            );
            return apiResponse.payload().rooms();
        } else {
            throw new IOException("Failed to get rooms: " + response.body());
        }
    }

    /**
     * 코드 퀴즈 방 생성
     */
    public CodeQuizRoomDTO createCodeQuizRoom(String roomName, int maxPlayers, Language language) throws IOException, InterruptedException {
        CreateRoomRequest request = new CreateRoomRequest(roomName, maxPlayers, language);
        String json = objectMapper.writeValueAsString(request);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/api/games/code-quiz/rooms"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            ApiResponse<CodeQuizRoomDTO> apiResponse = objectMapper.readValue(
                response.body(),
                new TypeReference<ApiResponse<CodeQuizRoomDTO>>() {}
            );
            return apiResponse.payload();
        } else {
            throw new IOException("Failed to create room: " + response.body());
        }
    }
}
