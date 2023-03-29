package pl.pacinho.thousand.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.pacinho.thousand.model.enums.GameStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class GameDto {

    private String id;
    private GameStatus status;
    private List<String> players;
    private LocalDateTime startTime;

    private List<CardDto> cards;
    private MusikInfoDto musikInfoDto;
    private Map<Integer, PlayerInfo> playersInfo;
    private int playersCount;
    private Integer actualPlayer;
    private Integer playerIndex;

    public int getNextPlayer(int offset) {
        int idx = playerIndex + offset;
        if (idx > playersCount) return idx - playersCount;
        return idx;
    }
}