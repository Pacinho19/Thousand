package pl.pacinho.thousand.model.dto.mapper;

import pl.pacinho.thousand.model.dto.*;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.utils.AuctionUtils;
import pl.pacinho.thousand.utils.GameUtils;

import java.util.*;
import java.util.stream.Collectors;

public class GameDtoMapper {

    public static GameDto parse(Game game, String name) {
        Integer playerIndex = getPlayerIndex(game.getPlayers(), name);
        return GameDto.builder()
                .id(game.getId())
                .startTime(game.getStartTime())
                .players(game.getPlayers().stream().map(Player::getName).sorted().toList())
                .status(game.getStatus())
                .stage(game.getStage())
                .cards(
                        getCards(game.getPlayers(), name)
                )
                .wonCards(
                        getWonCards(name, game)
                )
                .playersCount(game.getPlayersCount())
                .playersInfo(
                        getPlayersInfo(game.getPlayers())
                )
                .playerIndex(
                        playerIndex
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
                .roundSuit(game.getRoundSuit())
                .playerRoundPoints(
                        getPlayerRoundPoints(game, playerIndex)
                )
                .build();
    }

    private static int getPlayerRoundPoints(Game game, Integer playerIdx) {
        if (playerIdx == null)
            return 0;

        return GameUtils.calculatePlayerRoundPoints(
                game.getPlayers().get(playerIdx - 1).getRoundSummaryDto()
        );
    }

    private static List<CardDto> getWonCards(String name, Game game) {
        if (game.getStage() != GameStage.GAME_ON)
            return Collections.emptyList();

        RoundSummaryDto roundSummaryDto = game.getPlayers().stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .get()
                .getRoundSummaryDto();

        if (roundSummaryDto == null)
            return Collections.emptyList();

        roundSummaryDto.getCards().sort(Comparator.comparing(CardDto::getSuit).thenComparing(c -> c.getRank().getValue()));
        return roundSummaryDto.getCards();
    }

    private static LinkedList<CardDto> getStack(Game game) {
        return new LinkedList<>(
                game.getStack().values()
        );
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
                        p -> new PlayerInfo(p.getName(), p.getCards().size(), p.getPoints())
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