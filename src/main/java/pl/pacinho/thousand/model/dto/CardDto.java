package pl.pacinho.thousand.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.pacinho.thousand.model.enums.CardRank;
import pl.pacinho.thousand.model.enums.CardSuit;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CardDto {

    private CardSuit suit;
    private CardRank rank;


}