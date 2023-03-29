package pl.pacinho.thousand.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.pacinho.thousand.model.dto.GameDto;
import pl.pacinho.thousand.model.dto.mapper.GameDtoMapper;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.enums.GameStatus;
import pl.pacinho.thousand.repository.GameRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GameLogicService gameLogicService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public List<GameDto> getAvailableGames() {
        return gameRepository.getAvailableGames();
    }

    public String newGame(String name, int playersCount) {
        List<GameDto> activeGames = getAvailableGames();
        if (activeGames.size() >= 10)
            throw new IllegalStateException("Cannot create new Game! Active game count : " + activeGames.size());

        String gameId = gameRepository.newGame(name, playersCount);
        simpMessagingTemplate.convertAndSend("/game-created", true);
        return gameId;
    }

    public GameDto findDtoById(String gameId, String name) {
        return GameDtoMapper.parse(gameLogicService.findById(gameId));
    }

    public void joinGame(String name, String gameId) throws IllegalStateException {
        Game game = gameRepository.joinGame(name, gameId);
        if (game.getPlayers().size() == game.getPlayersCount()) game.setStatus(GameStatus.IN_PROGRESS);
    }

    public boolean checkStartGame(String gameId) {
        Game game = gameLogicService.findById(gameId);
        return game.getPlayers().size() == game.getPlayersCount();
    }

    public boolean canJoin(GameDto game, String name) {
        return game.getPlayers().size() < game.getPlayersCount() && game.getPlayers().stream().noneMatch(p -> p.equals(name));
    }
}