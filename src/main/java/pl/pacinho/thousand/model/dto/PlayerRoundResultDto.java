package pl.pacinho.thousand.model.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
public final class PlayerRoundResultDto {
    private String name;
    private  int points;
    private  boolean bomb;
    @Setter
    private  boolean ready;

    public PlayerRoundResultDto(String name, int points, boolean bomb) {
        this.name = name;
        this.points = points;
        this.bomb = bomb;
        this.ready = false;
    }
}