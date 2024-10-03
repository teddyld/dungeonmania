package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MercenaryTest {

    @Test
    @Tag("12-1")
    @DisplayName("Test mercenary in line with Player moves towards them")
    public void simpleMovement() {
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        // P1       P2      P3      P4      M4      M3      M2      M1      .      Wall
        //                                  Wall    Wall   Wall    Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_simpleMovement", "c_mercenaryTest_simpleMovement");

        assertEquals(new Position(8, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(7, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(6, 1), getMercPos(res));
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), getMercPos(res));
    }

    @Test
    @Tag("12-2")
    @DisplayName("Test mercenary stops if they cannot move any closer to the player")
    public void stopMovement() {
        //                  Wall     Wall    Wall
        // P1       P2      Wall      M1     Wall
        //                  Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_stopMovement", "c_mercenaryTest_stopMovement");

        Position startingPos = getMercPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getMercPos(res));
    }

    @Test
    @Tag("12-3")
    @DisplayName("Test mercenaries can not move through closed doors")
    public void doorMovement() {
        //                  Wall     Door    Wall
        // P1       P2      Wall      M1     Wall
        // Key              Wall     Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_doorMovement", "c_mercenaryTest_doorMovement");

        Position startingPos = getMercPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(startingPos, getMercPos(res));
    }

    @Test
    @Tag("12-4")
    @DisplayName("Test mercenary moves around a wall to get to the player")
    public void evadeWall() {
        //                  Wall      M2
        // P1       P2      Wall      M1
        //                  Wall      M2
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_evadeWall", "c_mercenaryTest_evadeWall");

        res = dmc.tick(Direction.RIGHT);
        assertTrue(new Position(4, 1).equals(getMercPos(res))
            || new Position(4, 3).equals(getMercPos(res)));
    }

    @Test
    @Tag("12-5")
    @DisplayName("Testing a mercenary can be bribed with a certain amount")
    public void bribeAmount() {
        //                                                          Wall     Wall     Wall    Wall    Wall
        // P1       P2/Treasure      P3/Treasure    P4/Treasure      M4       M3       M2     M1      Wall
        //                                                          Wall     Wall     Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeAmount", "c_mercenaryTest_bribeAmount");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up first treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // pick up second treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(6, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // pick up third treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(5, 1), getMercPos(res));

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("12-6")
    @DisplayName("Testing a mercenary can be bribed within a radius")
    public void bribeRadius() {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1       P2/Treasure      P3    P4      M4       M3       M2     M1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_bribeRadius", "c_mercenaryTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // attempt bribe
        assertThrows(InvalidActionException.class, () ->
                dmc.interact(mercId)
        );
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("12-7")
    @DisplayName("Testing an allied mercenary does not battle the player")
    public void allyBattle() {
        //                                  Wall    Wall    Wall
        // P1       P2/Treasure      .      M2      M1      Wall
        //                                  Wall    Wall    Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyBattle", "c_mercenaryTest_allyBattle");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // achieve bribe
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // walk into mercenary, a battle does not occur
        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("12-8")
    @DisplayName("Testing an allied mercenary in line with Player moves towards them")
    public void allyMove() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMove", "c_mercenaryTest_allyMovement");

        // Pickup treasure
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Achieve bribe
        String mercId = TestUtils.getEntityAtPos(res, "mercenary", new Position(7, 3)).get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(6, 3)));
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(5, 3)));
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(4, 3)));
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(3, 3)));
    }

    @Test
    @Tag("12-9")
    @DisplayName("Test an adjacent allied mercenary does not move if the Player walks into a wall")
    public void allyStopWall() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMove", "c_mercenaryTest_allyMovement");

        // Pickup treasure
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Achieve bribe
        String mercId = TestUtils.getEntityAtPos(res, "mercenary", new Position(7, 3)).get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Move allied mercenary adjacent to Player
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Try moving into the Wall 10 times
        for (int i = 0; i < 10; i++) {
            res = dmc.tick(Direction.DOWN);
            assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(2, 2)));
        }
    }

    @Test
    @Tag("12-10")
    @DisplayName("Test an adjacent allied mercenary does not move if the Player bribes another mercenary")
    public void allyStopBribe() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMove", "c_mercenaryTest_allyMovement");

        // Pickup treasure
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Achieve bribe
        String mercId1 = TestUtils.getEntityAtPos(res, "mercenary", new Position(7, 3)).get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId1));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Move allied mercenary to Player's previous distinct position
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(2, 2)));

        // Bribe another mercenary
        String mercId2 = TestUtils.getEntityAtPos(res, "mercenary", new Position(2, 5)).get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId2));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(2, 2)));
    }

    @Test
    @Tag("12-11")
    @DisplayName("Test an adjacent allied mercenary does not move if the Player destroys a spawner")
    public void allyStopSpawner() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMoveSpawner", "c_mercenaryTest_allyMovement");

        // Pickup treasure and sword
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Achieve bribe
        String mercId = TestUtils.getEntityAtPos(res, "mercenary", new Position(7, 3)).get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Move allied mercenary to Player's previous distinct position
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(2, 2)));

        // Destroy the spawner
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));

        assertTrue(TestUtils.entityAtPosition(res, "mercenary", new Position(2, 2)));
    }

    @Test
    @Tag("12-12")
    @DisplayName("Test an adjacent allied mercenary occupies the position the Player was previously in when moving")
    public void allyFollow() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyMove", "c_mercenaryTest_allyMovement");

        // Pickup treasure
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Achieve bribe
        String mercId = TestUtils.getEntityAtPos(res, "mercenary", new Position(7, 3)).get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        res = dmc.tick(Direction.RIGHT);

        Position playerPosition1 = TestUtils.getPlayerPos(res);
        assertEquals(new Position(3, 3), playerPosition1);

        // Player moves cardinally adjacent to allied mercenary
        res = dmc.tick(Direction.RIGHT);
        // Allied mercenary occupies the Player's previous positions
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition1));

        Position playerPosition2 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition2));

        Position playerPosition3 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition3));

        Position playerPosition4 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.LEFT);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition4));

        Position playerPosition5 = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.UP);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", playerPosition5));
    }

    @Test
    @Tag("12-13")
    @DisplayName("Test allied mercenary movement when Player was previously entering a portal")
    public void allyPortal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyPortal", "c_mercenaryTest_allyMovement");

        // Pickup treasure
        res = dmc.tick(Direction.DOWN);

        // Achieve bribe
        String mercId = TestUtils.getEntityAtPos(res, "mercenary", new Position(11, 2)).get().getId();
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Player moves into the portal
        Position beforeTeleportPosition = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.DOWN);
        assertTrue(TestUtils.entityAtPosition(res, "mercenary", beforeTeleportPosition));
    }


    @Test
    @Tag("12-14")
    @DisplayName("Test that multiple allies adjacent to the Player overlap on the square the Player was previously in.")
    public void allyOverlap() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_mercenaryTest_allyOverlap", "c_mercenaryTest_allyMovement");

        // Pickup treasures
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        // Bribe each mercenary
        List<String> mercsIds = TestUtils.getEntitiesStream(res, "mercenary")
                                         .map(e -> e.getId())
                                         .collect(Collectors.toList());

        for (String id : mercsIds) {
            res = assertDoesNotThrow(() -> dmc.interact(id));
        }

        // Each mercenary should now be cardinally adjacent to the Player
        Position previousPlayerPosition = TestUtils.getPlayerPos(res);
        res = dmc.tick(Direction.UP);

        // The mercenaries overlap on the previous Player's position
        List<Position> mercPositions = TestUtils.getEntityPositions(res, "mercenary");
        for (Position mercPosition : mercPositions) {

            assertEquals(previousPlayerPosition, mercPosition);
        }
    }


    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }
}
