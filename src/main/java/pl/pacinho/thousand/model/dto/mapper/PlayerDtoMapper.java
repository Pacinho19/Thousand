package pl.pacinho.thousand.model.dto.mapper;

import pl.pacinho.thousand.model.dto.PlayerDto;
import pl.pacinho.thousand.model.entity.Player;

public class PlayerDtoMapper {
    public static PlayerDto parse(Player player) {
        return new PlayerDto(
                player.getName(),
                player.getAuctionOffer(),
                player.getPoints()
        );
    }
}