package dungeonmania.entities.enemies;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Sceptre;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Mercenary extends Enemy implements Interactable {
    public static final int DEFAULT_BRIBE_AMOUNT = 1;
    public static final int DEFAULT_BRIBE_RADIUS = 1;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_HEALTH = 10.0;

    private int bribeAmount;
    private int bribeRadius;
    private boolean allied = false;
    private boolean following = false;

    public Mercenary(Position position, double health, double attack, int bribeAmount, int bribeRadius) {
        super(position, health, attack);
        this.bribeAmount = bribeAmount;
        this.bribeRadius = bribeRadius;
    }

    public boolean isAllied() {
        return allied;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setAlly() {
        this.allied = true;
    }

    public void setHostile() {
        this.allied = false;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (allied) return;
        super.onOverlap(map, entity);
    }

    /**
     * check whether the current merc can be bribed
     * @param player
     * @return
     */
    private boolean canBeBribed(Player player) {
        Position distance = Position.calculatePositionBetween(player.getPosition(), getPosition());

        return Math.abs(distance.getX()) <= bribeRadius
            && Math.abs(distance.getY()) <= bribeRadius
            && player.countEntityOfType(Treasure.class) >= bribeAmount;
    }

    /**
     * bribe the merc
     */
    public void bribe(Player player) {
        for (int i = 0; i < bribeAmount; i++) {
            player.use(Treasure.class);
        }

    }

    @Override
    public void interact(Player player, Game game) {
        if (player.hasSceptre()) {
            Sceptre sceptre = player.getInventoryEntities(Sceptre.class).get(0);
            player.use(sceptre, this, game.getTick());
        } else {
            setAlly();
            bribe(player);
        }
    }

    @Override
    public void move(Game game) {
        if (stuck()) return;
        Player player = game.getPlayer();
        GameMap map = game.getMap();

        checkCardinallyAdjacentPlayer(player);

        if (allied && following) {
            setPosition(player.getPreviousDistinctPosition());
        } else {
            Position nextPos = map.dijkstraPathFind(getPosition(), map.getPlayerPosition(), this);
            map.moveTo(this, nextPos);
            checkCardinallyAdjacentPlayer(player);
        }
    }

    @Override
    public boolean isInteractable(Player player) {
        return !allied && (canBeBribed(player) || player.hasSceptre());
    }

    public void checkCardinallyAdjacentPlayer(Player player) {
        List<Position> validPositions = player.getCardinallyAdjacentPositions();
        for (Position position : validPositions) {
            if (getPosition().equals(position)) {
                this.following = true;
            }
        }
    }

    @Override
    public boolean stuck() {
        if (isAllied() && isFollowing()) return false;
        return super.stuck();
    }
}
