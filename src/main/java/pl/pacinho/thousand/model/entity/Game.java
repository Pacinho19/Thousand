package pl.pacinho.thousand.model.entity;

import lombok.Getter;
import lombok.Setter;
import pl.pacinho.thousand.model.enums.GameStatus;

import java.time.LocalDateTime;
import java.util.*;

@Getter
public class Game {

    private String id;
    @Setter
    private GameStatus status;
    private LinkedList<Player> players;
    private LocalDateTime startTime;
    private int playersCount;

    public Game(String player1, int playersCount) {
        this.playersCount = playersCount;
        players = new LinkedList<>();
        players.add(new Player(player1, 1));
        this.id = UUID.randomUUID().toString();
        this.status = GameStatus.NEW;
        this.startTime = LocalDateTime.now();
    }

}
