package dungeonmania.entities;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwampTile extends Entity {
    public static final int DEFAULT_MOVEMENT_FACTOR = 2;
    private int movementFactor;

    public SwampTile(Position position, int movementFactor) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        this.movementFactor = movementFactor;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                Mercenary merc = (Mercenary) entity;
                if (merc.isAllied() && merc.isFollowing()) return;
            }
            ((Enemy) entity).swamped(movementFactor);
        }
    }
}
