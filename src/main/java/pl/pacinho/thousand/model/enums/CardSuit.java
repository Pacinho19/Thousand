package pl.pacinho.thousand.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum CardSuit {

    CLUBS(CardColor.BLACK, "bi bi-suit-club-fill", 60),
    DIAMONDS(CardColor.RED, "bi bi-suit-diamond-fill", 80),
    HEARTS(CardColor.RED, "bi bi-suit-heart-fill", 100),
    SPADES(CardColor.BLACK, "bi bi-suit-spade-fill", 40);

    @Getter
    private final CardColor color;
    @Getter
    private final String iconClassName;
    @Getter
    private final int points;
}
