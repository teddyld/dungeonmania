package dungeonmania.entities.enemies;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Enemy extends Entity implements Battleable {
    private BattleStatistics battleStatistics;
    private int swampDebuff = 0;

    public Enemy(Position position, double health, double attack) {
        super(position.asLayer(Entity.CHARACTER_LAYER));
        battleStatistics = new BattleStatistics(
                health,
                attack,
                0,
                BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
                BattleStatistics.DEFAULT_ENEMY_DAMAGE_REDUCER);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return entity instanceof Player || entity instanceof Enemy;
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

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            map.battle(player, this);
        }
    }

    @Override
    public void onDestroy(GameMap map) {
        Game g = map.getGame();
        g.unsubscribe(getId());
        map.getPlayer().incEnemiesDestroyed();
    }

    public abstract void move(Game game);

    public void moveRandom(Game game) {
        Position nextPos;
        GameMap map = game.getMap();
        Random randGen = new Random();
        List<Position> pos = getCardinallyAdjacentPositions();
        pos = pos
            .stream()
            .filter(p -> map.canMoveTo(this, p)).collect(Collectors.toList());
        nextPos = pos.size() == 0 ? getPosition() : pos.get(randGen.nextInt(pos.size()));
        map.moveTo(this, nextPos);
    }

    public void swamped(int movementFactor) {
        this.swampDebuff = movementFactor;
    }

    public boolean stuck() {
        if (swampDebuff > 0) {
            swampDebuff--;
            return true;
        }
        return false;
    }

}
