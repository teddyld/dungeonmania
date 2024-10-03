package dungeonmania.entities.enemies;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.Entity;
import dungeonmania.util.Position;


public class Spider extends Enemy {

    private List<Position> movementTrajectory;
    private int nextPositionElement;
    private int direction;

    public static final int DEFAULT_SPAWN_RATE = 0;
    public static final double DEFAULT_ATTACK = 5;
    public static final double DEFAULT_HEALTH = 10;

    public Spider(Position position, double health, double attack) {
        super(position, health, attack);
        /**
         * Establish spider movement trajectory Spider moves as follows:
         *  8 1 2       10/12  1/9  2/8
         *  7 S 3       11     S    3/7
         *  6 5 4       B      5    4/6
         */
        movementTrajectory = position.getAdjacentPositions();
        nextPositionElement = 1;
        direction = 1;
    };

    private void updateNextPosition() {
        nextPositionElement = Math.floorMod(nextPositionElement + direction, 8);
    }

    @Override
    public void move(Game game) {
        if (stuck()) return;
        Position nextPos = movementTrajectory.get(nextPositionElement);
        List<Entity> entities = game.getMapEntities(nextPos);
        if (entities != null && entities.size() > 0 && entities.stream().anyMatch(e -> e instanceof Boulder)) {
            direction *= -1;
            updateNextPosition();
            updateNextPosition();
        }
        nextPos = movementTrajectory.get(nextPositionElement);
        game.getMap().moveTo(this, nextPos);
        updateNextPosition();
    }

    @Override
    public void moveRandom(Game game) {
        return;
    }
}
