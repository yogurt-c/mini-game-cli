package io.yogurt.cli_mini_game.game.controller;

import io.yogurt.cli_mini_game.common.api.ApiResponse;
import io.yogurt.cli_mini_game.common.game.dto.GameTypeListResponse;
import io.yogurt.cli_mini_game.game.service.GameTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
@Slf4j
public class GameTypeController {

    private final GameTypeService gameTypeService;

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<GameTypeListResponse>> getGameTypes() {
        log.debug("GET /api/games/types - Fetching available game types");

        GameTypeListResponse response = gameTypeService.getAvailableGameTypes();

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}