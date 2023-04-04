package pl.pacinho.thousand.model.dto;

import pl.pacinho.thousand.model.enums.Place;

public record PlayerSummaryDto(String name, int points) {

    public Place getPlace(int number){
        return Place.findByNumber(number);
    }

}
