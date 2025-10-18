package io.yogurt.cli_mini_game.common.game.dto;

import java.util.List;

public record GameTypeListResponse(
    List<GameTypeDTO> gameTypes
) {

}
