package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SceptreTest {
    @Test
    @Tag("20-1")
    @DisplayName("Test sceptre can be built from 1 wood, 1 key and 1 sun stone")
    public void build1Wood1Key1Sunstone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_build", "c_sceptreTest_duration5");

        // Pickup wood -> key -> sun stone
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));


        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("20-2")
    @DisplayName("Test sceptre can be built from 1 wood, 1 treasure and 1 sun stone")
    public void build1Wood1Treasure1Sunstone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_build", "c_sceptreTest_duration5");

        // Pickup wood -> treasure -> sun stone
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));


        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("20-3")
    @DisplayName("Test sceptre can be built from 2 arrows, 1 key and 1 sun stone")
    public void build2Arrows1Key1Sunstone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_build", "c_sceptreTest_duration5");

        // Pickup arrow -> arrow -> key -> sun stone
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);

        assertEquals(2, TestUtils.getInventory(res, "arrow").size());
        assertEquals(1, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));


        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("20-4")
    @DisplayName("Test sceptre can be built from 2 arrows, 1 treasure and 1 sun stone")
    public void build2Arrows1Treasure1Sunstone() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_build", "c_sceptreTest_duration5");

        // Pickup arrow -> arrow -> treasure -> sun stone
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(2, TestUtils.getInventory(res, "arrow").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("20-5")
    @DisplayName("Test sceptre can be built from 1 wood, 2 sun stones")
    public void build1Wood2Sunstones() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_build", "c_sceptreTest_duration5");

        // Pickup wood -> sun stone -> sun stone
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("20-6")
    @DisplayName("Test sceptre can be built from 2 arrows, 2 sun stones")
    public void build2Arrows2Sunstones() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_build", "c_sceptreTest_duration5");

        // Pickup arrow -> arrow -> sun stone -> sun stone
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(2, TestUtils.getInventory(res, "arrow").size());
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        assertEquals(1, TestUtils.getInventory(res, "sceptre").size());
        assertEquals(0, TestUtils.getInventory(res, "arrow").size());
        assertEquals(2, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("20-7")
    @DisplayName("Test that a mind-controlled mercenary does not battle the player")
    public void allyBattle() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_mindControl", "c_sceptreTest_duration5");

        // Pickup wood -> treasure -> sun stone and build sceptre
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        // Mind control the mercenary
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("20-8")
    @DisplayName("Test that an adjacent mind-controlled mercenary follows the previous position of the Player")
    public void allyFollow() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_mindControl", "c_sceptreTest_duration50");

        // Pickup wood -> treasure -> sun stone and build sceptre
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        // Mind control the mercenary
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Mercenary is considered allied and will follow the Player
        Position playerPosition1 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.UP);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition1));

        Position playerPosition2 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition2));

        Position playerPosition3 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition3));

        Position playerPosition4 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition4));
    }

    @Test
    @Tag("20-9")
    @DisplayName("Test that a mind-controller mercenary battles the player when the effect ends")
    public void effectEnd() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sceptreTest_mindControl", "c_sceptreTest_duration5");

        // Pickup wood -> treasure -> sun stone and build sceptre
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        res = assertDoesNotThrow(() -> dmc.build("sceptre"));

        // Mind control the mercenary
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Duration of mind control effect is 5 ticks
        for (int i = 0; i < 5; i++) {
            res = dmc.tick(Direction.DOWN);
        }

        // Walk into the now hostile mercenary starts a battle
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, res.getBattles().size());
    }
}
