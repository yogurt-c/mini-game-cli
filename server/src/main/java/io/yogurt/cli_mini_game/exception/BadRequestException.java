package io.yogurt.cli_mini_game.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends CustomException {

    public BadRequestException(String message,
                               String errorCode) {
        super(HttpStatus.BAD_REQUEST, message, errorCode);
    }
}
