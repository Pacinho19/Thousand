package pl.pacinho.thousand.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@RequiredArgsConstructor
public enum CardRank {

    NINE("9", null,0),
    TEN("10", null,10),
    JACK("J", null,2),
    QUEEN("Q","K", 3),
    KING("K", "Q",4),
    ACE("A", null,11);

    @Getter
    private final String name;
    private final String pair;
    @Getter
    private final int value;

    private static CardRank findByName(String name){
        return Stream.of(CardRank.values())
                .filter(cr -> cr.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->  new IllegalArgumentException("Invalid card rank name: " + name));
    }

    public CardRank getPair(){
        return findByName(pair);
    }

}