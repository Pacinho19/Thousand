package pl.pacinho.thousand.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.thousand.model.dto.CardDto;
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
    private List<CardDto> stack;

    public Game(String player1, int playersCount) {
        this.playersCount = playersCount;
        players = new LinkedList<>();
        players.add(new Player(player1, 1));
        this.id = UUID.randomUUID().toString();
        this.status = GameStatus.NEW;
        this.startTime = LocalDateTime.now();
        this.actualPlayer=1;
        this.musik = new ArrayList<>();
        this.stack = new ArrayList<>();
    }
    public void addCardToStack(CardDto cardDto) {
        this.stack.add(cardDto);
    }
    public void clearStack() {
        this.stack.clear();
    }

    public void addCardToMusik(CardDto cardDto) {
        this.musik.add(cardDto);
    }
}
