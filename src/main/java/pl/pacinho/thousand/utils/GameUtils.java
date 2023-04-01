package pl.pacinho.thousand.utils;

import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;

import java.util.List;

public class GameUtils {

    public static Player getPlayer(Game game, String name) {
        return game.getPlayers()
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown player: " + name));
    }

    public static CardDto findCard(List<CardDto> cards, CardDto card) {
        return cards.stream()
                .filter(c -> c.equals(card))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Card " + card + "not found in player cards "));
    }

    public static boolean allPlayersHasTheSameCardsCount(Game game) {
        return game.getPlayers()
                .stream()
                .map(p -> p.getCards().size())
                .distinct()
                .count() == 1;
    }

    public static boolean checkPlayer(Game game, String name) {
        return game.getPlayers().get(game.getActualPlayer() - 1)
                .getName().equals(name);
    }

    public static boolean checkCard(List<CardDto> cards, CardDto cardDto) {
        return cards.stream()
                .anyMatch(c -> c.equals(cardDto));
    }

    public static Player getActualPlayer(Game game) {
        return game.getPlayers().get(
                game.getActualPlayer() - 1
        );
    }

    public static Player getPlayerByIndex(Game game, int number) {
        return game.getPlayers()
                .get(number - 1);
    }

    public static int getNexPlayerIdx(int playersCount, int actualIdx) {
        if (actualIdx == playersCount - 1)
            return 0;
        return actualIdx + 1;
    }

    public static boolean allPlayersHasNoCards(Game game) {
        return game.getPlayers().stream()
                .allMatch(p -> p.getCards().isEmpty());
    }
}