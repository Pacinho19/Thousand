package pl.pacinho.thousand.model.dto;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RoundResultDto {
    private String message;
    private List<PlayerRoundResultDto> playersResult;

    public RoundResultDto(String message) {
        this.message = message;
        this.playersResult = new ArrayList<>();
    }

    public void addPlayerResult(PlayerRoundResultDto playerResult) {
        this.playersResult.add(playerResult);
    }
}