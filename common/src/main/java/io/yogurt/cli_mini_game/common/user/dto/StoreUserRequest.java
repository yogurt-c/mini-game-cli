package io.yogurt.cli_mini_game.common.user.dto;

public record StoreUserRequest(
    String nickname,
    String username,
    String password
) {

}
