package io.yogurt.cli_mini_game.game.codequiz.domain;

public enum RoomStatus {
    WAITING,    // 대기 중 (입장 가능)
    IN_GAME,    // 게임 진행 중 (입장 불가)
    FINISHED    // 게임 종료
}
