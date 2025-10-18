package io.yogurt.cli_mini_game.game.service;

import io.yogurt.cli_mini_game.common.game.dto.GameTypeDTO;
import io.yogurt.cli_mini_game.common.game.dto.GameTypeListResponse;
import io.yogurt.cli_mini_game.game.entity.GameType;
import io.yogurt.cli_mini_game.game.repository.GameTypeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GameTypeService {

    private final GameTypeRepository gameTypeRepository;

    public GameTypeListResponse getAvailableGameTypes() {
        log.debug("Fetching available game types");

        List<GameType> gameTypes = gameTypeRepository.findAll();

        List<GameTypeDTO> gameTypeDTOs = gameTypes.stream()
            .map(this::toDTO)
            .collect(Collectors.toList());

        log.debug("Found {} game types", gameTypeDTOs.size());
        return new GameTypeListResponse(gameTypeDTOs);
    }

    private GameTypeDTO toDTO(GameType gameType) {
        return new GameTypeDTO(
            gameType.getId(),
            gameType.getCode(),
            gameType.getName(),
            gameType.getDescription()
        );
    }
}