package dungeonmania.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable {
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 5.0;
    public static final double DEFAULT_ALLY_ATTACK = 1.0;
    public static final double DEFAULT_ALLY_DEFENCE = 1.0;
    private BattleStatistics battleStatistics;
    private Inventory inventory;
    private Queue<Potion> potionQueue = new LinkedList<>();
    private Queue<Mercenary> sceptreQueue = new LinkedList<>();
    private Potion inEffective = null;
    private Mercenary controlledUnit = null;
    private int nextTriggerPotion = 0;
    private int nextTriggerSceptre = 0;
    private int enemiesDestroyed = 0;
    private double allyAttack;
    private double allyDefence;

    public Player(Position position, double health, double attack, double allyAttack, double allyDefence) {
        super(position);
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
        inventory = new Inventory();
        this.allyAttack = allyAttack;
        this.allyDefence = allyDefence;
    }

    public boolean hasWeapon() {
        return inventory.hasWeapon();
    }

    public BattleItem getWeapon() {
        return inventory.getWeapon();
    }

    public void useWeapon(Game game) {
        getWeapon().use(game);
    }

    public List<String> getBuildables(int numZombies) {
        return inventory.getBuildables(numZombies);
    }

    public boolean build(String entity, EntityFactory factory, int numZombies) {
        InventoryItem item = inventory.buildItem(this, entity, factory, numZombies);
        if (item == null) return false;
        return inventory.add(item);
    }

    public void move(GameMap map, Direction direction) {
        this.setFacing(direction);
        map.moveTo(this, Position.translateBy(this.getPosition(), direction));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            if (entity instanceof Mercenary) {
                if (((Mercenary) entity).isAllied()) return;
            }
            map.battle(this, (Enemy) entity);
        }
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public Entity getEntity(String itemUsedId) {
        return inventory.getEntity(itemUsedId);
    }

    public boolean pickUp(Entity item) {
        return inventory.add((InventoryItem) item);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Potion getEffectivePotion() {
        return inEffective;
    }

    public <T extends InventoryItem> void use(Class<T> itemType) {
        T item = inventory.getFirst(itemType);
        if (item != null) inventory.remove(item);
    }

    public void use(Bomb bomb, GameMap map) {
        inventory.remove(bomb);
        bomb.onPutDown(map, getPosition());
    }

    public void triggerNextPotion(int currentTick) {
        if (potionQueue.isEmpty()) {
            inEffective = null;
            return;
        }
        inEffective = potionQueue.remove();
        nextTriggerPotion = currentTick + inEffective.getDuration();
    }

    public void triggerNextSceptre(int currentTick) {
        if (sceptreQueue.isEmpty()) {
            controlledUnit = null;
            return;
        }

        Sceptre sceptre = getInventoryEntities(Sceptre.class).get(0);
        controlledUnit = sceptreQueue.remove();
        nextTriggerSceptre = currentTick + sceptre.getDuration();
    }

    public void use(Potion potion, int tick) {
        inventory.remove(potion);
        potionQueue.add(potion);
        if (inEffective == null) {
            triggerNextPotion(tick);
        }
    }

    public void use(Sceptre sceptre, Mercenary mercenary, int tick) {
        sceptreQueue.add(mercenary);
        if (controlledUnit == null) {
            mercenary.setAlly();
            triggerNextSceptre(tick);
        }
    }

    public void onTickPotion(int tick) {
        if (inEffective == null || tick == nextTriggerPotion) {
            triggerNextPotion(tick);
        }
    }

    public void onTickSceptre(int tick) {
        if (controlledUnit == null || tick == nextTriggerSceptre) {
            if (controlledUnit != null) {
                controlledUnit.setHostile();
            }
            triggerNextSceptre(tick);
        }
    }

    public void remove(InventoryItem item) {
        inventory.remove(item);
    }

    @Override
    public BattleStatistics getBattleStatistics() {
        return battleStatistics;
    }

    public double getHealth() {
        return battleStatistics.getHealth();
    }

    public void setHealth(double health) {
        battleStatistics.setHealth(health);
    }

    public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
        return inventory.count(itemType);
    }

    public <T> List<T> getInventoryEntities(Class<T> clz) {
        return inventory.getEntities(clz);
    }

    public void incEnemiesDestroyed() {
        enemiesDestroyed += 1;
    }

    public int getEnemiesDestroyed() {
        return enemiesDestroyed;
    }

    public BattleStatistics applyAllyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            this.allyAttack,
            this.allyDefence,
            1,
            1));
    }

    public boolean hasSceptre() {
        return countEntityOfType(Sceptre.class) >= 1;
    }
}
