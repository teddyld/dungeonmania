package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SunStoneTest {
    @Test
    @Tag("15-1")
    @DisplayName("Test that sun stones can be used to open doors")
    public void door() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_door", "c_sunStoneTest_basic");
        // Player has just spawned and sun stone is still on the ground
        assertEquals(new Position(1, 1), getPlayerPos(res));
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 1), getPlayerPos(res));
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Unlock door, sun stone not used
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 1), getPlayerPos(res));
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // Walk past door
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), getPlayerPos(res));
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
    }

    @Test
    @Tag("15-2")
    @DisplayName("Test that sun stones can be used to build entities")
    public void build() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_build", "c_sunStoneTest_basic");

        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(2, TestUtils.getEntities(res, "wood").size());

        // Pick up sun stone and both wood entities
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(2, TestUtils.getInventory(res, "wood").size());

        // Shield is built, sun stone is not consumed
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
    }

    @Test
    @Tag("15-3")
    @DisplayName("Test that sun stones cannot be used to bribe mercenaries")
    public void bribe() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_bribe", "c_sunStoneTest_basic");
        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );

    }

    @Test
    @Tag("15-4")
    @DisplayName("Test that sun stones count towards the treasure goal")
    public void treasure() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_sunStoneTest_treasure", "c_sunStoneTest_basic");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());

        // Pick up sun stone
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    private Position getPlayerPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "player").get(0).getPosition();
    }
}
