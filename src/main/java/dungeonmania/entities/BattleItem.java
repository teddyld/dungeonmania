package dungeonmania.entities;

import dungeonmania.Game;
import dungeonmania.battles.BattleStatistics;
import java.io.Serializable;

/**
 * Item has buff in battles
 */
public interface BattleItem extends Serializable {
    public BattleStatistics applyBuff(BattleStatistics origin);
    public void use(Game game);
    public int getDurability();
}
