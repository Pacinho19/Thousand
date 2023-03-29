package pl.pacinho.thousand.repository;

import org.springframework.stereotype.Repository;
import pl.pacinho.thousand.exception.GameNotFoundException;
import pl.pacinho.thousand.model.dto.GameDto;
import pl.pacinho.thousand.model.dto.mapper.GameDtoMapper;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;
import pl.pacinho.thousand.model.enums.GameStatus;

import java.util.*;

@Repository
public class GameRepository {

    private Map<String, Game> gameMap;

    public GameRepository() {
        gameMap = new HashMap<>();
    }

    public String newGame(String playerName, int playersCount) {
        Game game = new Game(playerName, playersCount);
        gameMap.put(game.getId(), game);
        return game.getId();
    }

    public List<GameDto> getAvailableGames() {
        return gameMap.values()
                .stream()
                .filter(game -> game.getStatus() != GameStatus.FINISHED)
                .map(g -> GameDtoMapper.parse(g, null))
                .sorted(Comparator.comparing(GameDto::getStartTime).reversed())
                .toList();
    }

    public Optional<Game> findById(String gameId) {
        return Optional.ofNullable(gameMap.get(gameId));
    }

    public Game joinGame(String name, String gameId) throws IllegalStateException {
        Game game = gameMap.get(gameId);
        if (game == null)
            throw new GameNotFoundException(gameId);

        if (game.getStatus() != GameStatus.NEW)
            throw new IllegalStateException("Cannot join to " + gameId + ". Game status : " + game.getStatus());

        if (game.getPlayers().get(0).getName().equals(name))
            throw new IllegalStateException("Game " + gameId + " was created by you!");

        game.getPlayers().add(new Player(name, game.getPlayers().size() + 1));
        return game;
    }

}
