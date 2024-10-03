package dungeonmania;

import java.util.List;
import java.util.PriorityQueue;
import java.util.UUID;
import java.io.Serializable;

import dungeonmania.battles.BattleFacade;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.SunStone;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.ZombieToast;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.goals.Goal;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Game implements Serializable {
    private String id;
    private String name;
    private Goal goals;
    private GameMap map;
    private Player player;
    private BattleFacade battleFacade;
    private int initialTreasureCount;
    private int initialSunStoneCount;
    private EntityFactory entityFactory;
    private boolean isInTick = false;
    public static final int PLAYER_MOVEMENT = 0;
    public static final int PLAYER_MOVEMENT_CALLBACK = 1;
    public static final int AI_MOVEMENT = 2;
    public static final int AI_MOVEMENT_CALLBACK = 3;

    private int tickCount = 0;
    //private static Comparator<ComparableCallback> comp = Comparator.comparing(ComparableCallback::getV);
    private PriorityQueue<ComparableCallback> sub = new PriorityQueue<>(1, new MyComparator());
    private PriorityQueue<ComparableCallback> addingSub = new PriorityQueue<>(1, new MyComparator());

    public Game(String dungeonName) {
        this.name = dungeonName;
        this.map = new GameMap();
        this.battleFacade = new BattleFacade();
    }

    public void init() {
        this.id = UUID.randomUUID().toString();
        map.init();
        this.tickCount = 0;
        player = map.getPlayer();
        register((Runnable & Serializable)
            () -> player.onTickPotion(tickCount), PLAYER_MOVEMENT_CALLBACK, "potionQueue");
        register((Runnable & Serializable)
            () -> player.onTickSceptre(tickCount), AI_MOVEMENT_CALLBACK, "sceptreQueue");
        initialTreasureCount = map.getEntities(Treasure.class).size();
        initialSunStoneCount = map.getEntities(SunStone.class).size();

    }

    public Game tick(Direction movementDirection) {
        registerOnce((Runnable & Serializable)
            () -> player.move(this.getMap(), movementDirection), PLAYER_MOVEMENT, "playerMoves");
        tick();
        return this;
    }

    public Game tick(String itemUsedId) throws InvalidActionException {
        Entity item = player.getEntity(itemUsedId);
        if (item == null)
            throw new InvalidActionException(String.format("Item with id %s doesn't exist", itemUsedId));
        if (!(item instanceof Bomb) && !(item instanceof Potion))
            throw new IllegalArgumentException(String.format("%s cannot be used", item.getClass()));

        registerOnce((Runnable & Serializable) () -> {
            if (item instanceof Bomb)
                player.use((Bomb) item, map);
            if (item instanceof Potion)
                player.use((Potion) item, tickCount);
        }, PLAYER_MOVEMENT, "playerUsesItem");
        tick();
        return this;
    }

    /**
     * Battle between player and enemy
     * @param player
     * @param enemy
     */
    public void battle(Player player, Enemy enemy) {
        battleFacade.battle(this, player, enemy);
        if (player.getHealth() <= 0) {
            map.destroyEntity(player);
        }
        if (enemy.getHealth() <= 0) {
            map.destroyEntity(enemy);
        }
    }

    public Game build(String buildable) throws InvalidActionException {
        int numZombies = map.getEntities(ZombieToast.class).size();
        List<String> buildables = player.getBuildables(numZombies);
        if (!buildables.contains(buildable)) {
            throw new InvalidActionException(String.format("%s cannot be built", buildable));
        }
        registerOnce((Runnable & Serializable)
            () -> player.build(buildable, entityFactory, numZombies), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public Game interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity e = map.getEntity(entityId);
        if (e == null || !(e instanceof Interactable))
            throw new IllegalArgumentException("Entity cannot be interacted");
        if (!((Interactable) e).isInteractable(player)) {
            throw new InvalidActionException("Entity cannot be interacted");
        }
        registerOnce((Runnable & Serializable)
            () -> ((Interactable) e).interact(player, this), PLAYER_MOVEMENT, "playerBuildsItem");
        tick();
        return this;
    }

    public <T extends Entity> long countEntities(Class<T> type) {
        return map.countEntities(type);
    }

    public void register(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id));
        else
            sub.add(new ComparableCallback(r, priority, id));
    }

    public void registerOnce(Runnable r, int priority, String id) {
        if (isInTick)
            addingSub.add(new ComparableCallback(r, priority, id, true));
        else
            sub.add(new ComparableCallback(r, priority, id, true));
    }

    public void unsubscribe(String id) {
        for (ComparableCallback c : sub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
        for (ComparableCallback c : addingSub) {
            if (id.equals(c.getId())) {
                c.invalidate();
            }
        }
    }

    public int tick() {
        isInTick = true;
        sub.forEach(s -> s.run());
        isInTick = false;
        sub.addAll(addingSub);
        addingSub = new PriorityQueue<>();
        sub.removeIf(s -> !s.isValid());
        tickCount++;
        // update the weapons/potions duration
        return tickCount;
    }

    public int getTick() {
        return this.tickCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Goal getGoals() {
        return goals;
    }

    public void setGoals(Goal goals) {
        this.goals = goals;
    }

    public GameMap getMap() {
        return map;
    }

    public List<Entity> getMapEntities(Position p) {
        return map.getEntities(p);
    }

    public <T extends Entity> List<T> getMapEntities(Class<T> type) {
        return map.getEntities(type);
    }

    public void setMap(GameMap map) {
        this.map = map;
    }

    public EntityFactory getEntityFactory() {
        return entityFactory;
    }

    public void setEntityFactory(EntityFactory factory) {
        entityFactory = factory;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BattleFacade getBattleFacade() {
        return battleFacade;
    }

    public void setBattleFacade(BattleFacade battleFacade) {
        this.battleFacade = battleFacade;
    }

    public int getInitialTreasureCount() {
        return initialTreasureCount;
    }

    public int getInitialSunStoneCount() {
        return initialSunStoneCount;
    }

    public void removePlayerInvItem(InventoryItem item) {
        player.remove(item);
    }

}
