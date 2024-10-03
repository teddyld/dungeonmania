package dungeonmania.entities.buildables;

import dungeonmania.battles.BattleStatistics;

public class Sceptre extends Buildable {
    public static final int DEFAULT_DURATION = 5;
    private int mindControlDuration;

    public Sceptre(int mindControlDuration) {
        super();
        this.mindControlDuration = mindControlDuration;
    }

    public int getDuration() {
        return mindControlDuration;
    }

    @Override
    public BattleStatistics applyBuff(BattleStatistics origin) {
        return BattleStatistics.applyBuff(origin, new BattleStatistics(
            0,
            0,
            0,
            1,
            1));
    }
}
