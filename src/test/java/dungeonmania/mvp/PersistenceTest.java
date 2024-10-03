package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTest {

    @Test
    @Tag("19-1")
    @DisplayName("Test player position persists")
    public void testPlayerPosition() {
        // System.setProperty("sun.io.serialization.extendedDebugInfo", "true");
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_playerPosition", "c_persistenceTest_basic");

        // Player starts at (1, 1)
        Position pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(new Position(1, 1), pos);
        // Move to the right 3 times
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        pos = TestUtils.getEntities(res, "player").get(0).getPosition();
        assertEquals(new Position(4, 1), pos);
        // Save game at (4, 1)
        dmc.saveGame("test");

        // Move down 3 times
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(4, 4), TestUtils.getEntities(res, "player").get(0).getPosition());

        // Loads the previous save, player should be at (4, 1) instead of (4, 4)
        res = dmc.loadGame("test");
        assertEquals(pos, TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("19-2")
    @DisplayName("Test all enemy positions persist")
    public void testEnemyPosition() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_enemyPosition", "c_persistenceTest_basic");

        // Move away from enemies
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        // Store enemy locations and save game
        Position zombiePos = TestUtils.getEntities(res, "zombie_toast").get(0).getPosition();
        Position spiderPos = TestUtils.getEntities(res, "spider").get(0).getPosition();
        Position mercPos = TestUtils.getEntities(res, "mercenary").get(0).getPosition();
        dmc.saveGame("test");

        // Move away from enemies, enemies should be at a different position to before
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        assertNotEquals(zombiePos, TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());
        assertNotEquals(spiderPos, TestUtils.getEntities(res, "spider").get(0).getPosition());
        assertNotEquals(mercPos, TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // Load game, enemies should be where they were when the game was saved
        res = dmc.loadGame("test");

        assertEquals(zombiePos, TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());
        assertEquals(spiderPos, TestUtils.getEntities(res, "spider").get(0).getPosition());
        assertEquals(mercPos, TestUtils.getEntities(res, "mercenary").get(0).getPosition());
    }

    @Test
    @Tag("19-3")
    @DisplayName("Test battling an enemy brings them back after loading")
    public void testBattle() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_battle", "c_persistenceTest_basic");

        // Move towards mercenary and save
        res = dmc.tick(Direction.RIGHT);
        dmc.saveGame("test");

        assertEquals(1, TestUtils.getEntities(res, "mercenary").size());
        assertEquals(0, res.getBattles().size());

        // Move to the right and battle the mercenary
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "mercenary").size());
        assertEquals(1, res.getBattles().size());

        // Load game, the mercenary should be back, and no battles should have occurred
        res = dmc.loadGame("test");
        assertEquals(1, TestUtils.getEntities(res, "mercenary").size());
        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("19-4")
    @DisplayName("Test spider spawn interval progress persists")
    public void testSpiderInterval() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_playerPosition", "c_persistenceTest_spiderInterval");
        // Move right twice, third movement should spawn a spider
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "spider").size());
        dmc.saveGame("test");

        // Spider should spawn on the third tick
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());

        // Load the save, there should be no spiders
        res = dmc.loadGame("test");
        assertEquals(0, TestUtils.getEntities(res, "spider").size());

        // Spider should spawn again
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "spider").size());
    }

    @Test
    @Tag("19-5")
    @DisplayName("Test enemy ids are the same after load")
    public void testEnemyIds() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_enemyIds", "c_persistenceTest_basic");

        res = dmc.tick(Direction.LEFT);
        dmc.saveGame("test");
        String zombieId = TestUtils.getEntities(res, "zombie_toast").get(0).getId();
        String spiderId = TestUtils.getEntities(res, "spider").get(0).getId();
        String mercId = TestUtils.getEntities(res, "mercenary").get(0).getId();

        res = dmc.tick(Direction.LEFT);
        res = dmc.tick(Direction.LEFT);

        res = dmc.loadGame("test");

        assertEquals(zombieId, TestUtils.getEntities(res, "zombie_toast").get(0).getId());
        assertEquals(spiderId, TestUtils.getEntities(res, "spider").get(0).getId());
        assertEquals(mercId, TestUtils.getEntities(res, "mercenary").get(0).getId());
    }

    @Test
    @Tag("19-6")
    @DisplayName("Test collectables persist on the ground upon loading save")
    public void testCollectables() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_collectables", "c_persistenceTest_basic");

        // No collectables should have been picked up
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getEntities(res, "wood").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());

        dmc.saveGame("test");
        // Pick up the SunStone and sword
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getEntities(res, "wood").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());

        // Load save, all collectables should still be on the ground again
        res = dmc.loadGame("test");

        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getEntities(res, "wood").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());
    }

    @Test
    @Tag("19-7")
    @DisplayName("Test items persist in inventory")
    public void testInventory() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_collectables", "c_persistenceTest_basic");

        // No collectables should have been picked up
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getEntities(res, "wood").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());

        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Pick up the sun stone and sword
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getEntities(res, "wood").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());

        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        dmc.saveGame("test");

        // Pick up the wood and treasure
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sword").size());
        assertEquals(0, TestUtils.getEntities(res, "wood").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());

        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Inventory should revert back to before the wood and treasure was collected
        res = dmc.loadGame("test");
        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getEntities(res, "wood").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());

        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("19-8")
    @DisplayName("Test walls are in the same location")
    public void testWalls() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_walls", "c_persistenceTest_basic");
        // Dummy movement to change game state
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(6, TestUtils.getEntities(res, "wall").size());
        List<Position> walls1 = TestUtils.getEntityPositions(res, "wall");

        dmc.saveGame("test");

        // Dummy movement to change game state
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        res = dmc.loadGame("test");

        List<Position> walls2 = TestUtils.getEntityPositions(res, "wall");

        // Using assertListAreEqualIgnoringOrder from Assignment 1 tests
        assertTrue(walls1.size() == walls2.size()
                && walls1.containsAll(walls2) && walls2.containsAll(walls1));
    }

    @Test
    @Tag("19-9")
    @DisplayName("Test portals remain linked and can still teleport the player")
    public void testPortals() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_portals", "c_persistenceTest_basic");

        // Player moves right above the portal
        res = dmc.tick(Direction.DOWN);

        assertEquals(2, TestUtils.getEntities(res, "portal").size());
        assertEquals(new Position(1, 2), TestUtils.getEntities(res, "player").get(0).getPosition());

        dmc.saveGame("test");

        // Player moves into the portal, should appear above the other
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(7, 2), TestUtils.getEntities(res, "player").get(0).getPosition());

        // Load the save, player is above the first portal
        res = dmc.loadGame("test");
        assertEquals(2, TestUtils.getEntities(res, "portal").size());
        assertEquals(new Position(1, 2), TestUtils.getEntities(res, "player").get(0).getPosition());

        // Player moves into the portal, should appear above the other
        res = dmc.tick(Direction.DOWN);
        assertEquals(new Position(7, 2), TestUtils.getEntities(res, "player").get(0).getPosition());
    }

    @Test
    @Tag("19-10")
    @DisplayName("Test goal becomes unachieved upon loading")
    public void testGoal() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_goal", "c_persistenceTest_basic");

        // Move above treasure
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        dmc.saveGame("test");

        // Collect treasure, completing the goal
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals("", TestUtils.getGoals(res));

        // Load the save,
        res = dmc.loadGame("test");
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // Collect treasure, completing the goal
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);

        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("19-11")
    @DisplayName("Test entities destroyed by a bomb appear again upon loading")
    public void testBomb() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_bomb", "c_persistenceTest_basic");

        // Push boulder onto switch, pick up bomb, stand under activated switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(3, TestUtils.getEntities(res, "wall").size());
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        dmc.saveGame("test");

        // Place bomb and confirm it exploded
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());
        assertEquals(0, TestUtils.getEntities(res, "wall").size());

        // Load the save, bomb should not have exploded yet
        res = dmc.loadGame("test");

        assertEquals(3, TestUtils.getEntities(res, "wall").size());
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());

        // Place bomb and confirm it exploded
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());
        assertEquals(0, TestUtils.getEntities(res, "wall").size());
    }

    @Test
    @Tag("19-12")
    @DisplayName("Test a key can be used to open the same door")
    public void testKeyDoor() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_keyDoor", "c_persistenceTest_basic");

        // Pick up the key
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getInventory(res, "key").size());
        dmc.saveGame("test");

        // Open the door and walk past it
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(6, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertEquals(0, TestUtils.getInventory(res, "key").size());

        // Load the save, key should still be inventory
        res = dmc.loadGame("test");
        assertEquals(1, TestUtils.getInventory(res, "key").size());

        // Open the door and walk past it
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(new Position(6, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertEquals(0, TestUtils.getInventory(res, "key").size());
    }

    @Test
    @Tag("19-13")
    @DisplayName("Test a mercenary remains bribed if they were allied before saving")
    public void testMercenaryBribed() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_mercenaryBribed", "c_persistenceTest_basic");
        String mercId = TestUtils.getEntities(res, "mercenary").get(0).getId();

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Bribe mercenary
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        dmc.saveGame("test");

        // Walk into mercenary, battle should not occur as they are allied
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, res.getBattles().size());

        res = dmc.loadGame("test");
        assertEquals(new Position(2, 1), TestUtils.getEntities(res, "player").get(0).getPosition());

        // Walk into mercenary, battle should not occur as they are allied
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("19-14")
    @DisplayName("Test a mercenary can be rebribed if they weren’t allied before saving")
    public void testMercenaryRebribe() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_mercenaryBribed", "c_persistenceTest_basic");
        String mercId = TestUtils.getEntities(res, "mercenary").get(0).getId();

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        dmc.saveGame("test");

        // Bribe mercenary
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Walk into mercenary, battle should not occur as they are allied
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, res.getBattles().size());

        res = dmc.loadGame("test");
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // Bribe mercenary
        res = assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Walk into mercenary, battle should not occur as they are allied
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("19-15")
    @DisplayName("Test spawners persist after breaking and then loading")
    public void testSpawner() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_spawner", "c_persistenceTest_basic");
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();

        // Pick up sword
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        dmc.saveGame("test");

        // Break the spawner
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.getEntities(res, "zombie_toast_spawner").size());

        // Load the game, spawner
        res = dmc.loadGame("test");

        assertEquals(1, TestUtils.getInventory(res, "sword").size());
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.getEntities(res, "zombie_toast_spawner").size());
    }

    @Test
    @Tag("19-16")
    @DisplayName("Test invisibility effect remains if it was active when the player saved")
    public void testInvisibility() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_invisibility", "c_persistenceTest_basic");

        // Pick up potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "invisibility_potion").size());

        // Drink potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invisibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invisibility_potion").size());

        dmc.saveGame("test");

        // Walk into mercenary, battle should not occur as the player is invisible
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, res.getBattles().size());

        res = dmc.loadGame("test");

        // Walk into mercenary, battle should not occur as the player is still invisible
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, res.getBattles().size());
    }

    @Test
    @Tag("19-17")
    @DisplayName("Test invincibility effect remains if it was active when the player saved")
    public void testInvincibility() throws InvalidActionException {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_invincibility", "c_persistenceTest_basic");

        // Pick up potion
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "invincibility_potion").size());

        // Drink potion
        res = dmc.tick(TestUtils.getFirstItemId(res, "invincibility_potion"));
        assertEquals(0, TestUtils.getInventory(res, "invincibility_potion").size());

        dmc.saveGame("test");

        // Walk into mercenary, player is invincible
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, res.getBattles().size());
        BattleResponse battle = res.getBattles().get(0);
        int enemyHealth =
            Integer.parseInt(TestUtils.getValueFromConfigFile("mercenary_health", "c_persistenceTest_basic"));
        assertEquals(0, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.001);
        assertTrue(-battle.getRounds().get(0).getDeltaEnemyHealth() >= enemyHealth);

        // Load the save, battle should not have happened yet
        res = dmc.loadGame("test");

        assertEquals(0, res.getBattles().size());

        // Walk into mercenary, player is invincible
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, res.getBattles().size());
        battle = res.getBattles().get(0);
        assertEquals(0, battle.getRounds().get(0).getDeltaCharacterHealth(), 0.001);
        assertTrue(-battle.getRounds().get(0).getDeltaEnemyHealth() >= enemyHealth);
    }

    @Test
    @Tag("19-18")
    @DisplayName("Test buildable entities are unbuilt and can be rebuilt")
    public void testBuildable() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_buildable", "c_persistenceTest_basic");

        // Pick up 2 wood entities
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(2, TestUtils.getInventory(res, "wood").size());
        assertEquals(1, TestUtils.getEntities(res, "treasure").size());
        dmc.saveGame("test");

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());

        // Build shield
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());

        // Load the save, undoing the last item pickup and building
        res = dmc.loadGame("test");

        assertEquals(0, TestUtils.getInventory(res, "shield").size());

        // Pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(0, TestUtils.getEntities(res, "treasure").size());

        // Build shield
        res = assertDoesNotThrow(() -> dmc.build("shield"));
        assertEquals(1, TestUtils.getInventory(res, "shield").size());
        assertEquals(0, TestUtils.getInventory(res, "wood").size());
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
    }

    @Test
    @Tag("19-19")
    @DisplayName("Test save doesn't exist")
    public void testNotExist() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_persistenceTest_buildable", "c_persistenceTest_basic");

        assertThrows(IllegalArgumentException.class, () -> dmc.loadGame("kjdhakudhaih27idhka2hdk2hd"));

    }
}
