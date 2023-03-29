package pl.pacinho.thousand.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.stereotype.Service;
import pl.pacinho.thousand.exception.GameNotFoundException;
import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.enums.CardRank;
import pl.pacinho.thousand.model.enums.CardSuit;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.repository.GameRepository;

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

}