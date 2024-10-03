package dungeonmania.entities.collectables;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Key extends Collectable {
    private int number;

    public Key(Position position, int number) {
        super(position);
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            if (player.getInventoryEntities(Key.class).size() == 0) {
                player.pickUp(this);
                map.destroyEntity(this);
            }
        }
    }
}
