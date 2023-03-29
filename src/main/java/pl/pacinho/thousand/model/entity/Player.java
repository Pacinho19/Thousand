package pl.pacinho.thousand.model.entity;

import lombok.Getter;

@Getter
public class Player {
    private final String name;
    private int index;
    private int completedRounds;
    private int points;
    private long summaryTime;

    public Player(String name, int index) {
        this.name = name;
        this.index = index;
        this.completedRounds = 0;
        this.points = 0;
        this.summaryTime = 0L;
    }

    public void incrementCompletedRounds() {
        this.completedRounds++;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void incrementSummaryTime(long time) {
        summaryTime += time;
    }
}