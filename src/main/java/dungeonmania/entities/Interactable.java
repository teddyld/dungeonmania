package dungeonmania.entities;

import dungeonmania.Game;
import java.io.Serializable;

public interface Interactable extends Serializable {
    public void interact(Player player, Game game);
    public boolean isInteractable(Player player);
}
