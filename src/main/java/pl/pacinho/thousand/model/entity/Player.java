package pl.pacinho.thousand.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.thousand.model.dto.CardDto;

import java.util.Collections;
import java.util.List;

@Getter
public class Player {
    private final String name;
    private int index;
    @Setter
    private List<CardDto> cards;
    private int points;

    public Player(String name, int index) {
        this.name = name;
        this.index = index;
        this.points = 0;
        this.cards = Collections.emptyList();
    }

    public void addPoints(int points) {
        this.points += points;
    }

}