package pl.pacinho.thousand.utils;

import pl.pacinho.thousand.model.dto.AuctionDto;
import pl.pacinho.thousand.model.dto.AuctionOfferDto;
import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;
import pl.pacinho.thousand.model.enums.CardRank;

import java.util.List;

public class AuctionUtils {
    public static boolean checkPlayerCanAuction(Game game, String name) {
        AuctionDto auctionDto = game.getAuctionDto();
        if (auctionDto == null)
            return false;

        if (auctionDto.getHighestOffer() < 120)
            return true;

        return auctionDto.getHighestOffer() < getPlayerCardsValue(game, name);
    }

    public static Integer getPlayerCardsValue(Game game, String name) {
        if (game.getAuctionDto() == null) return 0;
        if (name == null) return 0;

        Player player = GameUtils.getPlayer(game, name);
        return player.getCards()
                .stream()
                .map(card -> calculateValue(card, player.getCards()))
                .reduce(120, Integer::sum);
    }

    private static Integer calculateValue(CardDto card, List<CardDto> cards) {
        if (card.getRank() != CardRank.QUEEN)
            return 0;

        return cards.stream()
                .anyMatch(c -> c.getSuit() == card.getSuit() && c.getRank() == card.getRank().getPair())
                ? card.getSuit().getPoints()
                : 0;
    }

    public static boolean checkEndAuction(Game game) {
        AuctionDto auctionDto = game.getAuctionDto();
        if (auctionDto == null)
            return false;

        return auctionDto.getOffersMap().values()
                       .stream()
                       .filter(AuctionOfferDto::pass)
                       .count() == game.getPlayersCount() - 1;
    }

    public static Player getWiningPlayer(Game game) {
        String playerName = game.getAuctionDto()
                .getOffersMap()
                .entrySet()
                .stream()
                .filter(entry -> !entry.getValue().pass())
                .findFirst()
                .get()
                .getKey();

        return GameUtils.getPlayer(game, playerName);
    }
}
