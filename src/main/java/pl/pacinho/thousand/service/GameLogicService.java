package pl.pacinho.thousand.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import pl.pacinho.thousand.exception.GameNotFoundException;
import pl.pacinho.thousand.model.dto.*;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;
import pl.pacinho.thousand.model.enums.CardRank;
import pl.pacinho.thousand.model.enums.CardSuit;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.model.enums.GameStatus;
import pl.pacinho.thousand.repository.GameRepository;
import pl.pacinho.thousand.utils.AuctionUtils;
import pl.pacinho.thousand.utils.GameUtils;
import pl.pacinho.thousand.utils.NumberUtils;

import java.util.*;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class GameLogicService {

    private final GameRepository gameRepository;
    private final List<CardDto> CARDS_DECK = initCards();

    public Game findById(String gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException(gameId))
                ;
    }

    private List<CardDto> initCards() {
        return Arrays.stream(CardSuit.values())
                .flatMap(cs -> Arrays.stream(CardRank.values())
                        .map(cr -> new CardDto(cs, cr)))
                .toList();
    }

    public void dealTheCards(Game game) {
        List<CardDto> cards = new ArrayList<>(CARDS_DECK);
        Collections.shuffle(cards);

        drawMusik(game, cards);

        Stack<List<CardDto>> cardParts = partitionCards(cards, game.getPlayersCount());
        game.getPlayers()
                .forEach(p -> p.setCards(new LinkedList<>(cardParts.pop())));

        game.setStage(GameStage.AUCTION);

        setAuction(game);
    }

    private static void setAuction(Game game) {
        AuctionDto auctionDto = new AuctionDto(100, GameUtils.getNexPlayerIdx(game.getPlayersCount(), game.getRoundPlayer() - 1) + 1);
        auctionDto.addOffer(game.getPlayers().get(auctionDto.getCurrentPLayer() - 1).getName(), new AuctionOfferDto(100, false));
        game.setActualPlayer(game.getNextPlayer(2));
        game.setAuctionDto(auctionDto);
    }

    private Stack<List<CardDto>> partitionCards(List<CardDto> cards, int size) {
        List<List<CardDto>> partition = ListUtils.partition(cards, (cards.size() / size));
        Stack<List<CardDto>> stack = new Stack<>();
        stack.addAll(partition);
        return stack;
    }

    private void drawMusik(Game game, List<CardDto> cards) {
        game.getMusik().clear();
        IntStream.rangeClosed(1, 3)
                .forEach(i -> {
                    CardDto cardDto = cards.get(0);
                    game.addCardToMusik(cardDto);
                    cards.remove(cardDto);
                });
    }

    public void setNextAuctionPlayer(Game game) {
        int nextPlayerIdx = GameUtils.getNexPlayerIdx(game.getPlayersCount(), game.getActualPlayer() - 1);
        while (true) {
            Player player = game.getPlayers().get(nextPlayerIdx);
            AuctionOfferDto playerOffer = game.getAuctionDto().getOffersMap().get(player.getName());
            if (playerOffer == null || !playerOffer.pass()) {
                game.setActualPlayer(nextPlayerIdx + 1);
                break;
            }

            nextPlayerIdx = GameUtils.getNexPlayerIdx(game.getPlayersCount(), nextPlayerIdx);
        }
    }

    public void endAuction(Game game) {
        game.setStage(GameStage.END_AUCTION);
        Player player = AuctionUtils.getWiningPlayer(game);
        player.addCards(game.getMusik());
        game.setAuctionSummary(new AuctionSummaryDto(player.getName(), game.getAuctionDto().getHighestOffer()));
        game.setRoundPoints(game.getAuctionDto().getHighestOffer());
        game.setActualPlayer(player.getIndex());
        game.setAuctionDto(null);
    }

    public void checkBattle(Game game) {
        Map<Player, CardDto> stack = game.getStack();

        Player actualPlayer = GameUtils.getPlayerByIndex(game, game.getNextPlayer(1));
        CardSuit roundSuit = stack.get(actualPlayer).getSuit();
        boolean superCardSuit = game.getSuperCardSuit() != null && checkStackContainsSuperCard(game.getSuperCardSuit(), stack);
        Player winPlayer = stack.entrySet()
                .stream()
                .filter(c -> battleCardsFilter(c, roundSuit, superCardSuit, game.getSuperCardSuit()))
                .max(Comparator.comparing(entry -> entry.getValue().getRank().getValue()))
                .get()
                .getKey();

        winPlayer.getRoundSummaryDto().addCards(game.getStack().values());
        game.setActualPlayer(winPlayer.getIndex());
        game.clearStack();
        game.setRoundSuit(null);
    }

    private boolean battleCardsFilter(Map.Entry<Player, CardDto> c, CardSuit roundSuit, boolean superCardSuit, CardSuit cardSuit) {
        return (!superCardSuit && c.getValue().getSuit() == roundSuit)
               || (superCardSuit && c.getValue().getSuit() == cardSuit);
    }

    private boolean checkStackContainsSuperCard(CardSuit superCardSuit, Map<Player, CardDto> stack) {
        return stack.entrySet()
                .stream()
                .anyMatch(es -> es.getValue().getSuit() == superCardSuit);
    }

    public void checkSuperCardCheckIn(CardDto cardDto, Game game, Player player) {
        if (cardDto.getRank() != CardRank.QUEEN && cardDto.getRank() != CardRank.KING)
            return;

        if (!game.getStack().isEmpty())
            return;

        if (!checkCardPair(cardDto, player.getCards()))
            return;

        player.getRoundSummaryDto().addCheckInValue(cardDto.getSuit().getPoints());
        game.setSuperCardSuit(cardDto.getSuit());
    }

    private boolean checkCardPair(CardDto card, List<CardDto> cards) {
        return cards.stream()
                .anyMatch(c -> c.getSuit() == card.getSuit()
                               && c.getRank() == card.getRank().getPair());
    }

    public boolean checkEndOfRound(Game game) {
        if (!GameUtils.allPlayersHasNoCards(game))
            return false;

        RoundResultDto roundResult = new RoundResultDto("Round over!");
        game.getPlayers()
                .forEach(p -> {
                    int points = calculatePoints(game.getAuctionSummary(), p, game.getRoundPoints());
                    roundResult.addPlayerResult(new PlayerRoundResultDto(p.getName(), points, false));
                });

        finishRound(game, roundResult);
        return true;
    }

    private void nextRound(Game game) {
        int nextPlayer = GameUtils.getNexPlayerIdx(game.getPlayersCount(), game.getRoundPlayer() - 1);
        game.setRoundPoints(0);
        game.setSuperCardSuit(null);
        game.setRoundPlayer(nextPlayer + 1);
        game.setActualPlayer(nextPlayer + 1);

        dealTheCards(game);
    }

    private int calculatePoints(AuctionSummaryDto auctionSummary, Player p, int roundPoints) {
        int points = GameUtils.calculatePlayerRoundPoints(p.getRoundSummaryDto());
        int outputPoints;
        if (auctionSummary.playerName().equals(p.getName())) {
            outputPoints = points >= roundPoints ? roundPoints : roundPoints * -1;
            p.addPoints(outputPoints, true);
        } else {
            outputPoints = NumberUtils.roundToNearest10(points);
            p.addPoints(outputPoints, false);
        }

        p.getCards().clear();
        p.setRoundSummaryDto(new RoundSummaryDto());
        p.setAuctionOffer(null);

        return outputPoints;
    }


    public void bomb(Game game, Player player) {
        RoundResultDto roundResult = new RoundResultDto(player.getName() + " used a bomb!");
        if (!player.isBomb()) {
            player.setBomb(true);
            addingPlayerEmptyResults(game, roundResult, player);
        } else {
            game.getPlayers()
                    .forEach(p -> {
                        if (p.getName().equals(player.getName())) {
                            int points = -1 * getPointsToSubtractAfterBomb(game);
                            p.addPoints(points, true);
                            roundResult.addPlayerResult(
                                    new PlayerRoundResultDto(p.getName(), points, true));
                        } else {
                            p.addPoints(60, false);
                            roundResult.addPlayerResult(
                                    new PlayerRoundResultDto(p.getName(), 60, false));
                        }
                    });
        }

        finishRound(game, roundResult);
    }

    private void addingPlayerEmptyResults(Game game, RoundResultDto roundResult, Player bombPlayer) {
        game.getPlayers()
                .forEach(p -> roundResult.addPlayerResult(
                        new PlayerRoundResultDto(p.getName(), 0, bombPlayer.getName().equals(p.getName())))
                );
    }

    private int getPointsToSubtractAfterBomb(Game game) {
        if (game.getAuctionSummary() == null && game.getAuctionSummary().value() == 100)
            return 100;

        return game.getAuctionSummary().value();
    }

    public boolean playerReady(String gameId, String name) {
        Game game = findById(gameId);
        game.getRoundResult().getPlayersResult()
                .stream()
                .filter(pr -> pr.getName().equals(name))
                .forEach(pr -> pr.setReady(true));

        if (!GameUtils.checkAllPlayersReady(game))
            return false;

        nextRound(game);
        return true;
    }

    private void finishRound(Game game, RoundResultDto roundResult) {
        game.setStage(GameStage.ROUND_OVER);
        game.setRoundResult(roundResult);

        if (GameUtils.checkEndGame(game))
            game.setStatus(GameStatus.FINISHED);
    }

}