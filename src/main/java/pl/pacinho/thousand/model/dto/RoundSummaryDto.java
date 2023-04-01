package pl.pacinho.thousand.model.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class RoundSummaryDto {

    private List<CardDto> cards;
    private int checkInValue;

    public RoundSummaryDto() {
        this.cards = new ArrayList<>();
        this.checkInValue = 0;
    }

    public void addCheckInValue(int value) {
        this.checkInValue += value;
    }

    public void addCards(Collection<CardDto> stack) {
        this.cards.addAll(stack);
    }
}