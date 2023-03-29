package pl.pacinho.thousand.model.dto.mapper;

import pl.pacinho.thousand.model.dto.GameDto;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;

public class GameDtoMapper {

    public static GameDto parse(Game game) {
        return GameDto.builder()
                .id(game.getId())
                .startTime(game.getStartTime())
                .players(game.getPlayers().stream().map(Player::getName).sorted().toList())
                .status(game.getStatus())
                .playersCount(game.getPlayersCount())
                .build();
    }
}