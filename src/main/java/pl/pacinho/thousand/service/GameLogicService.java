package pl.pacinho.thousand.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pacinho.thousand.exception.GameNotFoundException;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.repository.GameRepository;

@RequiredArgsConstructor
@Service
public class GameLogicService {

    private final GameRepository gameRepository;

    public Game findById(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId))
                ;
    }

}