package pl.pacinho.thousand.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import pl.pacinho.thousand.exception.GameNotFoundException;
import pl.pacinho.thousand.model.dto.AuctionDto;
import pl.pacinho.thousand.model.dto.AuctionOfferDto;
import pl.pacinho.thousand.model.dto.AuctionSummaryDto;
import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;
import pl.pacinho.thousand.model.enums.CardRank;
import pl.pacinho.thousand.model.enums.CardSuit;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.repository.GameRepository;
import pl.pacinho.thousand.utils.AuctionUtils;
import pl.pacinho.thousand.utils.GameUtils;

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

        AuctionDto auctionDto = new AuctionDto(100, game.getRoundPlayer());
        auctionDto.addOffer(game.getPlayers().get(game.getRoundPlayer()).getName(), new AuctionOfferDto(100, false));
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
        IntStream.rangeClosed(1, 3)
                .forEach(i -> {
                    CardDto cardDto = cards.get(0);
                    game.addCardToMusik(cardDto);
                    cards.remove(cardDto);
                });
    }

    public void setNextAuctionPlayer(Game game) {
        int nextPlayerIdx = GameUtils.getNexPlayerIdx(game.getPlayersCount(), game.getActualPlayer()-1);
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

        winPlayer.addRoundCards(game.getStack().values());
        game.setActualPlayer(winPlayer.getIndex());
        game.clearStack();
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
}