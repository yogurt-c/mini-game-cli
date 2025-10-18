package io.yogurt.cli_mini_game.common.game.dto.codequiz;

public record PlayerJoinedMessage(
    String messageType,
    String nickname,
    CodeQuizRoomDTO room
) {
    public PlayerJoinedMessage(String nickname, CodeQuizRoomDTO room) {
        this("PLAYER_JOINED", nickname, room);
    }
}
