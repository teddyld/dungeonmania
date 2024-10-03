package dungeonmania.goals;

import java.io.Serializable;

import dungeonmania.Game;
import dungeonmania.entities.enemies.ZombieToastSpawner;

public class EnemyGoal implements Goal, Serializable {

    private int target;

    public EnemyGoal(int target) {
        this.target = target;
    }

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        return game.getPlayer().getEnemiesDestroyed() >= target
            && game.getMapEntities(ZombieToastSpawner.class).size() == 0;
    }

    public String toString(Game game) {
        return achieved(game) ? "" : ":enemies";
    }
}
