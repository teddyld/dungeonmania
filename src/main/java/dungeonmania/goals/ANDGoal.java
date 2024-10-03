package dungeonmania.goals;

import java.io.Serializable;
import dungeonmania.Game;

public class ANDGoal implements Goal, Serializable {

    private Goal goal1;
    private Goal goal2;

    public ANDGoal(Goal goal1, Goal goal2) {
        this.goal1 = goal1;
        this.goal2 = goal2;
    }

    public boolean achieved(Game game) {
        if (game.getPlayer() == null) return false;
        return goal1.achieved(game) && goal2.achieved(game);
    }

    public String toString(Game game) {
        return achieved(game) ? ""
            : "(" + goal1.toString(game) + " AND " + goal2.toString(game) + ")";
    }
}
