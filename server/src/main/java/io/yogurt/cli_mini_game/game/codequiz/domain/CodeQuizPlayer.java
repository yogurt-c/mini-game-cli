package io.yogurt.cli_mini_game.game.codequiz.domain;

import lombok.Getter;

/**
 * In-Memory 플레이어 정보
 */
@Getter
public class CodeQuizPlayer {
    private final String sessionId;
    private final String nickname;
    private int obstacleCount;
    private boolean alive;
    private int score;  // 정답 수

    public CodeQuizPlayer(String sessionId, String nickname) {
        this.sessionId = sessionId;
        this.nickname = nickname;
        this.obstacleCount = 0;
        this.alive = true;
        this.score = 0;
    }

    public void addObstacle() {
        this.obstacleCount++;
    }

    public void removeObstacle() {
        if (obstacleCount > 0) {
            this.obstacleCount--;
        }
    }

    public void increaseScore() {
        this.score++;
    }

    public void setDead() {
        this.alive = false;
    }

    public boolean isDead() {
        return !alive;
    }

    public boolean isAlive() {
        return alive;
    }
}
