package pl.pacinho.thousand.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.thousand.model.dto.AuctionOfferDto;
import pl.pacinho.thousand.model.dto.CardDto;

import java.util.*;

@Getter
public class Player {
    private final String name;
    private int index;
    @Setter
    private List<CardDto> cards;
    private int points;
    @Setter
    private AuctionOfferDto auctionOffer;
    private List<CardDto> roundCards;

    public Player(String name, int index) {
        this.name = name;
        this.index = index;
        this.points = 0;
        this.cards = Collections.emptyList();
        this.roundCards = new ArrayList<>();
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void addCards(List<CardDto> cards) {
        this.cards.addAll(cards);
    }

    public void addRoundCards(Collection<CardDto> stack) {
        this.roundCards.addAll(stack);
    }
}