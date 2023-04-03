package pl.pacinho.thousand.model.dto;


public record PlayerInfo(String name, Integer cardsCount, Integer points, boolean bombUsed) {
}