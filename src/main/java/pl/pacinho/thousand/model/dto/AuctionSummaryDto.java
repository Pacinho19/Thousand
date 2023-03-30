package pl.pacinho.thousand.model.dto;

public record AuctionSummaryDto(String playerName, int value) {

    @Override
    public String toString() {
        return playerName + ": " + value;
    }
}
