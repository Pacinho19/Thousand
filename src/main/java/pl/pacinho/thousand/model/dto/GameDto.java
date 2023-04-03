package pl.pacinho.thousand.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.pacinho.thousand.model.enums.CardSuit;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.model.enums.GameStatus;
import pl.pacinho.thousand.utils.AuctionUtils;
import pl.pacinho.thousand.utils.GameUtils;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class GameDto {

    private String id;
    private GameStatus status;
    private GameStage stage;
    private List<String> players;
    private LocalDateTime startTime;

    private List<CardDto> cards;
    private List<CardDto> wonCards;
    private LinkedList<CardDto> stack;
    private MusikInfoDto musikInfoDto;
    private Map<Integer, PlayerInfo> playersInfo;
    private int playersCount;
    private Integer actualPlayer;
    private Integer playerIndex;
    private Integer roundPlayer;

    private AuctionDto auctionDto;
    private boolean canAuction;
    private AuctionSummaryDto auctionSummary;
    private int maxAuctionValue;
    private int roundPoints;
    private CardSuit superCardSuit;
    private CardSuit roundSuit;
    private int playerRoundPoints;
    private boolean bombUsed;

    public int getNextPlayer(int offset) {
        int idx = playerIndex + offset;
        if (idx > playersCount) return idx - playersCount;
        return idx;
    }

    public boolean canUse(CardDto cardDto){
        return GameUtils.checkCanThrow(this, cardDto);
    }

}