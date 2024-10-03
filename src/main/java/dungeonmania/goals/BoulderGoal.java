package dungeonmania.goals;

import java.io.Serializable;
import dungeonmania.Game;
import dungeonmania.entities.Switch;


public class BoulderGoal implements Goal, Serializable {

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        return game.getMapEntities(Switch.class).stream().allMatch(s -> s.isActivated());
    }

    public String toString(Game game) {
        return achieved(game) ? "" : ":boulders";
    }
}
