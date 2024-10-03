package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.entities.Player;
import dungeonmania.entities.enemies.Assassin;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AssassinTest {

    @Test
    @Tag("16-1")
    @DisplayName("Test bribing an assassin - seed 1")
    public void assassinBribeSeed1() {
        Assassin assassin = new Assassin(new Position(1, 1), 20.0, 20.0, 0, 10, 0.8, 1);
        Player player = new Player(new Position(2, 2), 10.0, 10.0, 3.0, 3.0);

        // The 5th bribe attempt succeeds
        for (int i = 0; i < 5; i++) {
            assassin.interact(player, null);
            if (i == 4) {
                assertTrue(assassin.isAllied());
            } else {
                assertFalse(assassin.isAllied());
            }
        }
    }

    @Test
    @Tag("16-2")
    @DisplayName("Test bribing an assassin - seed 5")
    public void assassinBribeSeed5() {
        Assassin assassin = new Assassin(new Position(1, 1), 20.0, 20.0, 0, 10, 0.9, 5);
        Player player = new Player(new Position(2, 2), 10.0, 10.0, 3.0, 3.0);


        // The 10th bribe attempt succeeds
        for (int i = 0; i < 10; i++) {
            assassin.interact(player, null);
            if (i == 9) {
                assertTrue(assassin.isAllied());
            } else {
                assertFalse(assassin.isAllied());
            }
        }
    }

    @Test
    @Tag("16-3")
    @DisplayName("Test assassin in line with Player moves towards them")
    public void assassinSimpleMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_simpleMovement", "c_assassinTest_simpleMovement");

        assertEquals(new Position(8, 1), getAssassinPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getAssassinPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getAssassinPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getAssassinPos(res));
    }

    @Test
    @Tag("16-4")
    @DisplayName("Test assassin stops if they cannot move any closer to the player")
    public void assassinStopMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_stopMovement", "c_assassinTest_stopMovement");

        Position startingPos = getAssassinPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getAssassinPos(res));
    }

    @Test
    @Tag("16-5")
    @DisplayName("Test assassins can not move through closed doors")
    public void assassinDoorMovement() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_doorMovement", "c_assassinTest_doorMovement");

        Position startingPos = getAssassinPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getAssassinPos(res));
    }

    @Test
    @Tag("16-6")
    @DisplayName("Test assassin moves around a wall to get to the player")
    public void assassinEvadeWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_evadeWall", "c_assassinTest_evadeWall");

        res = dmc.tick(Direction.RIGHT);
        assertTrue(new Position(4, 1).equals(getAssassinPos(res))
            || new Position(4, 3).equals(getAssassinPos(res)));
    }

    @Test
    @Tag("16-7")
    @DisplayName("Testing an assassin can be bribed with a certain amount")
    public void assassinBribeAmount() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_bribeAmount", "c_assassinTest_bribeAmount");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getAssassinPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(assId)
        );
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up second treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getAssassinPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(assId)
        );
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // pick up third treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getAssassinPos(res));

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("16-8")
    @DisplayName("Testing an assassin can be bribed within a radius")
    public void assassinBribeRadius() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_bribeRadius", "c_assassinTest_bribeRadius");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getAssassinPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(assId)
        );
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("16-9")
    @DisplayName("Testing an allied assassin does not battle the player")
    public void assassinAllyBattle() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_assassinTest_allyBattle", "c_assassinTest_allyBattle");

        String assId = TestUtils.getEntitiesStream(res, "assassin").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(assId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // walk into assassin, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    private Position getAssassinPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "assassin").get(0).getPosition();
    }
}
