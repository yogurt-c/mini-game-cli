package io.yogurt.cli_mini_game.client.util;

import io.yogurt.cli_mini_game.common.user.dto.UserInfoResponse;

/**
 * 클라이언트 사용자 세션 관리
 */
public class UserSession {

    private static UserSession instance;
    private UserInfoResponse userInfo;
    private String sessionId;

    private UserSession() {
    }

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void login(UserInfoResponse userInfo) {
        this.userInfo = userInfo;
    }

    public void logout() {
        this.userInfo = null;
        this.sessionId = null;
    }

    public boolean isLoggedIn() {
        return userInfo != null;
    }

    public UserInfoResponse getUserInfo() {
        return userInfo;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getNickname() {
        return userInfo != null ? userInfo.nickname() : null;
    }
}
