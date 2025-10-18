package io.yogurt.cli_mini_game.common.game.dto.codequiz;

public record CreateRoomRequest(
    String roomName,
    int maxPlayers,
    Language language  // "JAVA", "PYTHON"
) {

    public CreateRoomRequest {
        if (roomName == null || roomName.isBlank()) {
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        if (maxPlayers < 2 || maxPlayers > 10) {
            throw new IllegalArgumentException("Max players must be between 2 and 10");
        }
    }
}
