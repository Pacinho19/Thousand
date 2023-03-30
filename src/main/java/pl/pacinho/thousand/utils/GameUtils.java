package pl.pacinho.thousand.utils;

import pl.pacinho.thousand.model.entity.Game;
import pl.pacinho.thousand.model.entity.Player;

public class GameUtils {

    public static Player getPlayer(Game game, String name){
        return game.getPlayers()
                .stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown player: " + name));
    }
}
