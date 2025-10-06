package io.yogurt.cli_mini_game.common.api;

public record ApiResponse<T>(
    String message,
    T payload,
    String errorCode
) {

    private static String OK = "요청이 처리되었습니다.";

    public static <T> ApiResponse<T> ok(T payload) {

        return new ApiResponse<>(
            OK,
            payload,
            null
        );
    }
}
