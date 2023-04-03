package pl.pacinho.thousand.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.thousand.model.dto.AuctionOfferDto;
import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.dto.RoundSummaryDto;

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
    @Setter
    private RoundSummaryDto roundSummaryDto;
    @Setter
    private boolean bomb;

    public Player(String name, int index) {
        this.name = name;
        this.index = index;
        this.points = 0;
        this.cards = Collections.emptyList();
        this.roundSummaryDto = new RoundSummaryDto();
        this.bomb=false;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void addCards(List<CardDto> cards) {
        this.cards.addAll(cards);
    }

}