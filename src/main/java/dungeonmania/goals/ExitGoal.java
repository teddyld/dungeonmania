package dungeonmania.goals;

import java.util.List;
import java.io.Serializable;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class ExitGoal implements Goal, Serializable {

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        Player character = game.getPlayer();
        Position pos = character.getPosition();
        List<Exit> es = game.getMapEntities(Exit.class);
        if (es == null || es.size() == 0) return false;
        return es
            .stream()
            .map(Entity::getPosition)
            .anyMatch(pos::equals);
    }

    public String toString(Game game) {
        return achieved(game) ? "" : ":exit";
    }
}
