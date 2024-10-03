package dungeonmania.entities.enemies;

import java.util.Random;

import dungeonmania.Game;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {

    public static final double DEFAULT_ATTACK = 10.0;
    public static final double DEFAULT_HEALTH = 20.0;
    public static final double DEFAULT_FAIL_RATE = 0.5;
    private double bribeFailRate;
    private Random random;

    public Assassin(Position position, double health, double attack,
                    int bribeAmount, int bribeRadius, double bribeFailRate) {
        super(position, health, attack, bribeAmount, bribeRadius);
        this.bribeFailRate = bribeFailRate;
        this.random = new Random(System.currentTimeMillis());
    }

    public Assassin(Position position, double health, double attack,
                    int bribeAmount, int bribeRadius, double bribeFailRate, long seed) {
        super(position, health, attack, bribeAmount, bribeRadius);
        this.bribeFailRate = bribeFailRate;
        this.random = new Random(seed);
    }

    @Override
    public void interact(Player player, Game game) {
        bribe(player);
        if (random.nextDouble() > bribeFailRate) {
            setAlly();
        }
    }
}
