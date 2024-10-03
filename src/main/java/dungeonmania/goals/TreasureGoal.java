package dungeonmania.goals;

import java.io.Serializable;

import dungeonmania.Game;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.SunStone;

public class TreasureGoal implements Goal, Serializable {

    private int target;

    public TreasureGoal(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        return game.getInitialTreasureCount() - game.getMapEntities(Treasure.class).size()
             + game.getInitialSunStoneCount() - game.getMapEntities(SunStone.class).size() >= target;
    }

    public String toString(Game game) {
        return achieved(game) ? "" : ":treasure";
    }
}
