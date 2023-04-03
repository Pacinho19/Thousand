package pl.pacinho.thousand.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.thousand.model.dto.AuctionDto;
import pl.pacinho.thousand.model.dto.AuctionSummaryDto;
import pl.pacinho.thousand.model.dto.CardDto;
import pl.pacinho.thousand.model.dto.RoundResultDto;
import pl.pacinho.thousand.model.enums.CardSuit;
import pl.pacinho.thousand.model.enums.GameStage;
import pl.pacinho.thousand.model.enums.GameStatus;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Game {

    private String id;
    @Setter
    private GameStatus status;
    @Setter
    private GameStage stage;
    private LinkedList<Player> players;
    private LocalDateTime startTime;
    private int playersCount;
    @Setter
    private int actualPlayer;
    @Setter
    private List<CardDto> musik;
    @Setter
    private Map<Player, CardDto> stack;
    @Setter
    private int roundPlayer;
    @Setter
    private AuctionDto auctionDto;
    @Setter
    private AuctionSummaryDto auctionSummary;
    @Setter
    private int roundPoints;
    @Setter
    private CardSuit superCardSuit;
    @Setter
    private CardSuit roundSuit;
    @Setter
    private RoundResultDto roundResult;

    public Game(String player1, int playersCount) {
        this.playersCount = playersCount;
        players = new LinkedList<>();
        players.add(new Player(player1, 1));
        this.id = UUID.randomUUID().toString();
        this.status = GameStatus.NEW;
        this.startTime = LocalDateTime.now();
        this.actualPlayer = 1;
        this.roundPlayer = 1;
        this.musik = new ArrayList<>();
        this.stack = new HashMap<>();
    }

    public void addCardToStack(Player player, CardDto cardDto) {
        this.stack.put(player, cardDto);
    }

    public void clearStack() {
        this.stack.clear();
    }

    public void addCardToMusik(CardDto cardDto) {
        this.musik.add(cardDto);
    }

    public int getNextPlayer(int offset) {
        int idx = actualPlayer + offset;
        if (idx > playersCount) return idx - playersCount;
        return idx;
    }
}