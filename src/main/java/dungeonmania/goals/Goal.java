package dungeonmania.goals;

import dungeonmania.Game;
import java.io.Serializable;

public interface Goal extends Serializable {
    public boolean achieved(Game game);
    public String toString(Game game);
}
