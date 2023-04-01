package pl.pacinho.thousand.model.dto.mapper;

import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.dto.GameDto;
import pl.pacinho.thousand.model.dto.PlayerInfo;
import pl.pacinho.thousand.model.dto.MusikInfoDto;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;
import pl.pacinho.thousand.utils.AuctionUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GameDtoMapper {

    public static GameDto parse(Game game, String name) {
        return GameDto.builder()
                .id(game.getId())
                .startTime(game.getStartTime())
                .players(game.getPlayers().stream().map(Player::getName).sorted().toList())
                .status(game.getStatus())
                .stage(game.getStage())
                .cards(
                        getCards(game.getPlayers(), name)
                )
                .playersCount(game.getPlayersCount())
                .playersInfo(
                        getPlayersInfo(game.getPlayers())
                )
                .playerIndex(
                        getPlayerIndex(game.getPlayers(), name)
                )
                .actualPlayer(game.getActualPlayer())
                .musikInfoDto(
                        getMusikInfo(game)
                )
                .roundPlayer(game.getRoundPlayer())
                .roundPoints(game.getRoundPoints())
                .auctionDto(game.getAuctionDto())
                .canAuction(AuctionUtils.checkPlayerCanAuction(game, name))
                .auctionSummary(game.getAuctionSummary())
                .maxAuctionValue(AuctionUtils.getPlayerCardsValue(game, name))
                .stack(getStack(game))
                .superCardSuit(game.getSuperCardSuit())
                .build();
    }

    private static Map<String, CardDto> getStack(Game game) {
        return game.getStack()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().getName(),
                        Map.Entry::getValue
                ));
    }

    private static MusikInfoDto getMusikInfo(Game game) {
        if (game.getStage() == null) return null;

        switch (game.getStage()) {
            case AUCTION -> {
                return new MusikInfoDto(null, game.getMusik().size());
            }
            case END_AUCTION -> {
                return new MusikInfoDto(game.getMusik(), game.getMusik().size());
            }
            default -> {
                return null;
            }
        }
    }

    private static Integer getPlayerIndex(LinkedList<Player> players, String name) {
        if (name == null) return null;

        Optional<Player> playerOpt = players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();

        if (playerOpt.isEmpty()) return null;
        return playerOpt.get()
                .getIndex();
    }

    private static Map<Integer, PlayerInfo> getPlayersInfo(LinkedList<Player> players) {
        return players.stream()
                .collect(Collectors.toMap(
                        Player::getIndex,
                        p -> new PlayerInfo(p.getName(), p.getCards().size())
                ));
    }

    private static List<CardDto> getCards(LinkedList<Player> players, String name) {
        if (name == null) return Collections.emptyList();

        Optional<Player> playerOptional = players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst();

        if (playerOptional.isPresent()) {
            List<CardDto> cards = playerOptional.get().getCards();
            cards.sort(Comparator.comparing(CardDto::getSuit).thenComparing(c -> c.getRank().getValue()));
            return cards;
        }
        return Collections.emptyList();
    }
}