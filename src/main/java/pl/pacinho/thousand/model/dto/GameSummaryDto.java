package pl.pacinho.thousand.model.dto;

import java.util.List;

public record GameSummaryDto(String winner, List<PlayerSummaryDto> playersInfo) {
}
