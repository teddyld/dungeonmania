package dungeonmania.entities.buildables;

import dungeonmania.battles.BattleStatistics;
import dungeonmania.Game;

public class MidnightArmour extends Buildable {
    private double attack;
    private double defence;
    public static final double DEFAULT_ATTACK = 5.0;
    public static final double DEFAULT_DEFENCE = 5.0;

    public MidnightArmour(double attack, double defence) {
        super();
        this.attack = attack;
        this.defence = defence;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            attack,
            defence,
            1,
            1));
    }

    @Override
    public void use(Game game) {
        return;
    }

    @Override
    public int getDurability() {
        return 999;
    }
}
