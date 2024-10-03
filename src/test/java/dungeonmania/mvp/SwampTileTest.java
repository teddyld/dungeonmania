package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SwampTileTest {

    @Test
    @Tag("18-1")
    @DisplayName("Test that the swamp tile does not slow the movement of the player")
    public void swampTilePlayer() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_player", "c_swampTileTest_mf2");

        // Player moves into the swamp tile
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntityPos(res, "swamp_tile"), TestUtils.getPlayerPos(res));

        // Player moves off the swamp tile
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 2), TestUtils.getPlayerPos(res));
    }

    @Test
    @Tag("18-2")
    @DisplayName("Test that swamp tile slows the movement of hostile mercenaries")
    public void swampTileEnemyMercenary() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_mercenary", "c_swampTileTest_mf2");

        Position swampTilePos = TestUtils.getEntityPos(res, "swamp_tile");

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // Mercenary is stuck in the swamp tile for a duration of 2 ticks
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.DOWN);

        // Remaining duration: 1 tick
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.UP);

        // Remaining duration: 0 ticks
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.RIGHT);

        // Mercenary is free to move off the swamp tile
        assertNotEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        // Moves towards the player
        assertEquals(new Position(5, 3), TestUtils.getEntityPos(res, "mercenary"));
    }

    @Test
    @Tag("18-3")
    @DisplayName("Test that swamp tile slows the movement of spiders")
    public void swampTileSpider() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_spider", "c_swampTileTest_mf2");

        Position swampTilePos = TestUtils.getEntityPos(res, "swamp_tile");

        res = dmc.tick(Direction.DOWN);

        // Spider is stuck in the swamp tile and will be stuck for 2 ticks
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "spider"));

        Position nextSpiderPos = new Position(4, 2);

        res = dmc.tick(Direction.DOWN);
        assertEquals(TestUtils.getEntityPos(res, "swamp_tile"), TestUtils.getEntityPos(res, "spider"));

        res = dmc.tick(Direction.LEFT);
        assertEquals(TestUtils.getEntityPos(res, "swamp_tile"), TestUtils.getEntityPos(res, "spider"));

        // Spider is free and moves to its next position
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(swampTilePos, TestUtils.getEntityPos(res, "spider"));
        assertEquals(nextSpiderPos, TestUtils.getEntityPos(res, "spider"));
    }

    @Test
    @Tag("18-4")
    @DisplayName("Test that swamp tile slows the movement of zombies")
    public void swampTileZombies() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_zombie", "c_swampTileTest_mf2");

        Position swampTilePos = TestUtils.getEntityPos(res, "swamp_tile");

        res = dmc.tick(Direction.DOWN);

        // Zombie is stuck in the swamp tile and will be stuck for 2 ticks
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "zombie_toast"));

        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.getEntityPos(res, "swamp_tile"), TestUtils.getEntityPos(res, "zombie_toast"));

        res = dmc.tick(Direction.LEFT);
        assertEquals(TestUtils.getEntityPos(res, "swamp_tile"), TestUtils.getEntityPos(res, "zombie_toast"));

        // Zombie is free and moves to its next position
        res = dmc.tick(Direction.LEFT);
        assertNotEquals(swampTilePos, TestUtils.getEntityPos(res, "zombie_toast"));
    }

    @Test
    @Tag("18-5")
    @DisplayName("Test that swamp tile slows the movement of a non-adjacent allied mercenary")
    public void swampTileAlliedNonAdjacentMercenary() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_mercenary", "c_swampTileTest_mf2");

        Position swampTilePos = TestUtils.getEntityPos(res, "swamp_tile");

        // Pickup treasure and bribe mercenary
        res = dmc.tick(Direction.LEFT);
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Allied mercenary is stuck in the swamp tile for a duration of 2 ticks
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.DOWN);

        // Remaining duration: 1 tick
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.UP);

        // Remaining duration: 0 ticks
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.LEFT);

        // Mercenary is free to move off the swamp tile
        assertNotEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        // Moves towards the player
        assertEquals(new Position(5, 3), TestUtils.getEntityPos(res, "mercenary"));
    }

    @Test
    @Tag("18-6")
    @DisplayName("Test that the swamp tile does not slow the movement of an adjacent allied mercenary")
    public void swampTileAlliedAdjacentMercenary() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_mercenary", "c_swampTileTest_mf2");

        // Pickup treasure and bribe mercenary
        res = dmc.tick(Direction.RIGHT);
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));

        // Allied mercenary is adjacent to the player and in the swamp tile
        assertEquals(TestUtils.getEntityPos(res, "swamp_tile"), TestUtils.getEntityPos(res, "mercenary"));

        Position playerPrevPos = TestUtils.getPlayerPos(res);

        // Allied mercenary moves to the player's previous position
        res = dmc.tick(Direction.LEFT);
        assertEquals(playerPrevPos, TestUtils.getEntityPos(res, "mercenary"));
    }

    @Test
    @Tag("18-7")
    @DisplayName("Test that a mercenary is stuck in a swamp tile for one tick with a movement factor of 1")
    public void swampTileMF1() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_mercenary", "c_swampTileTest_mf1");

        Position swampTilePos = TestUtils.getEntityPos(res, "swamp_tile");

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // Mercenary is stuck in the swamp tile for a duration of 1 tick
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.RIGHT);

        // Remaining duration: 0 tick
        assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.LEFT);

        // Mercenary is free to move off the swamp tile
        assertNotEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));

        // Moves towards the player
        assertEquals(new Position(5, 3), TestUtils.getEntityPos(res, "mercenary"));
    }

    @Test
    @Tag("18-8")
    @DisplayName("Test that swamp tile slows the movement of multiple entities in it")
    public void swampTileMultiple() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_multiple", "c_swampTileTest_mf2");

        Position swampTilePos = TestUtils.getEntityPos(res, "swamp_tile");

        res = dmc.tick(Direction.LEFT);

        // Spider and Mercenary are stuck in the swamp tile for 2 ticks
        for (int i = 0; i < 2; i++) {
            assertEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));
            assertEquals(swampTilePos, TestUtils.getEntityPos(res, "spider"));
            res = dmc.tick(Direction.DOWN);
        }

        // Spider and mercenary are now free to move off the swamp tile
        res = dmc.tick(Direction.DOWN);
        assertNotEquals(swampTilePos, TestUtils.getEntityPos(res, "mercenary"));
        assertNotEquals(swampTilePos, TestUtils.getEntityPos(res, "spider"));
    }

    @Test
    @Tag("18-9")
    @DisplayName("Test if mercenaries take into account movement factor when pathfinding")
    public void swampTilePathing() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_pathing", "c_swampTileTest_mf5");

        // The path to the player through the swamp tile takes 7 ticks which is
        // longer than the 6 ticks required to go around the tile.

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(11, 1), TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(11, 0), TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(10, 0), TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(9, 0), TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(9, 1), TestUtils.getEntityPos(res, "mercenary"));

        res = dmc.tick(Direction.DOWN);

        // Battle occurs with the player after 6 ticks
        assertEquals(1, res.getBattles().size());
    }
}
