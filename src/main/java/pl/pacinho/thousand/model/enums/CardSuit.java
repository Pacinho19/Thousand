package pl.pacinho.thousand.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public enum CardSuit {

    CLUBS(CardColor.BLACK, "bi bi-suit-club-fill"),
    DIAMONDS(CardColor.RED, "bi bi-suit-diamond-fill"),
    HEARTS(CardColor.RED, "bi bi-suit-heart-fill"),
    SPADES(CardColor.BLACK, "bi bi-suit-spade-fill");

    @Getter
    private final CardColor color;
    @Getter
    private final String iconClassName;
}
