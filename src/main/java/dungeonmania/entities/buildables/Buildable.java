package dungeonmania.entities.buildables;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.util.Position;

public abstract class Buildable extends Entity implements InventoryItem, BattleItem {

    private int durability;

    public Buildable() {
        super(null);
    }

    public Buildable(Position position, int durability) {
        super(position);
        this.durability = durability;
    }

    public void use(Game game) {
        durability--;
        if (durability <= 0) {
            game.removePlayerInvItem(this);
        }
    }

    public int getDurability() {
        return durability;
    }
}
