package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BasicGoalsTest {

    @Test
    @Tag("13-1")
    @DisplayName("Test achieving a basic exit goal")
    public void exit() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_exit", "c_basicGoalsTest_exit");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":exit"));

        // move player to exit
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }


    @Test
    @Tag("13-2")
    @DisplayName("Test achieving a basic boulders goal")
    public void oneSwitch()  {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_oneSwitch", "c_basicGoalsTest_oneSwitch");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move boulder onto switch
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-3")
    @DisplayName("Test achieving a boulders goal where there are five switches")
    public void fiveSwitches()  {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_fiveSwitches", "c_basicGoalsTest_fiveSwitches");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move first four boulders onto switch
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":boulders"));

        // move last boulder onto switch
        res = dmc.tick(Direction.DOWN);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }


    @Test
    @Tag("13-4")
    @DisplayName("Test achieving a basic treasure goal")
    public void treasure() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_treasure", "c_basicGoalsTest_treasure");

        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(2, TestUtils.getInventory(res, "treasure").size());

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":treasure"));

        // collect treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(3, TestUtils.getInventory(res, "treasure").size());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-5")
    @DisplayName("Test enemies goal breaking a spawner")
    public void enemiesSpawner() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemies_spawner", "c_basicGoalsTest_enemies_spawner");
        String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
        // move player to right
        res = dmc.tick(Direction.RIGHT);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // spawner should be destroyed successfully
        res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
        assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-6")
    @DisplayName("Test enemies goal battling a spider")
    public void enemiesSpider() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemies_spider", "c_basicGoalsTest_enemies_spider");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // move player to right, battling the spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-7")
    @DisplayName("Test enemies goal battling a zombie")
    public void enemiesZombie() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemies_zombie", "c_basicGoalsTest_enemies_spider");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // move player to right, battling the spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-8")
    @DisplayName("Test enemies goal battling a mercenary")
    public void enemiesMercenary() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemies_mercenary", "c_basicGoalsTest_enemies_spider");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // move player to right, battling the spider
        res = dmc.tick(Direction.RIGHT);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-9")
    @DisplayName("Test enemies goal battling 3 spiders")
    public void enemiesSpiders() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemies_spiders", "c_basicGoalsTest_enemies_spiders");
        List<EntityResponse> entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 3);
        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        // move player to right, battling the first mercanary
        res = dmc.tick(Direction.LEFT);
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 2);

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));
        // move player to right, battling the second mercanary
        res = dmc.tick(Direction.LEFT);
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 1);

        // assert goal met (condition is 2 enemies)
        assertEquals("", TestUtils.getGoals(res));
        // move player to right, battling the third mercanary
        res = dmc.tick(Direction.LEFT);
        entities = res.getEntities();
        assertTrue(TestUtils.countEntityOfType(entities, "spider") == 0);

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-10")
    @DisplayName("Test enemies goal bombing a zombie")
    public void enemiesBombZombie() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemies_bombZombie", "c_basicGoalsTest_enemies_spider");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);

        // Place Bomb Adjacent to Switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }

    @Test
    @Tag("13-11")
    @DisplayName("Test enemies goal bombing a zombie toast spawner")
    public void enemiesBombSpawner() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_basicGoalsTest_enemies_bombSpawner", "c_basicGoalsTest_enemies_spawner");

        // assert goal not met
        assertTrue(TestUtils.getGoals(res).contains(":enemies"));

        // Activate Switch
        res = dmc.tick(Direction.RIGHT);
        // Pick up Bomb
        res = dmc.tick(Direction.DOWN);

        // Place Bomb Adjacent to Switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());

        // assert goal met
        assertEquals("", TestUtils.getGoals(res));
    }


}
