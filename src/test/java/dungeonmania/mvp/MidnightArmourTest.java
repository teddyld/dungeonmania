package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.RoundResponse;
import dungeonmania.util.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MidnightArmourTest {

    @Test
    @Tag("17-1")
    @DisplayName("Test midnight armour can be built")
    public void build() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_build", "c_midnightArmourTest_basic");
        // Sun stone and sword to the right of the player
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getEntities(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size());

        // Pick up both entities
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Build midnight armour
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));

        assertEquals(1, TestUtils.getInventory(res, "midnight_armour").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size());
    }

    @Test
    @Tag("17-2")
    @DisplayName("Test midnight armour cannot be built when zombies are present")
    public void buildZombie() {
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_buildZombie", "c_midnightArmourTest_basic");

        // Sun stone and sword to the right of the player
        assertEquals(1, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(0, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getEntities(res, "sword").size());
        assertEquals(0, TestUtils.getInventory(res, "sword").size());

        // Pick up both entities
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getEntities(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(0, TestUtils.getEntities(res, "sword").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());

        // Attempt to build armour, invalidated by zombie
        assertThrows(InvalidActionException.class, () ->
                dmc.build("midnight_armour")
        );

        assertEquals(0, TestUtils.getInventory(res, "midnight_armour").size());
        assertEquals(1, TestUtils.getInventory(res, "sun_stone").size());
        assertEquals(1, TestUtils.getInventory(res, "sword").size());
    }

    @Test
    @Tag("17-3")
    @DisplayName("Test midnight armour buffs player armour and defense")
    public void battle() {
        DungeonManiaController dmc = new DungeonManiaController();
        String config = "c_midnightArmourTest_basic";
        DungeonResponse res = dmc.newGame("d_midnightArmourTest_battle", config);

        // Pick up both entities and craft midnight armour
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = assertDoesNotThrow(() -> dmc.build("midnight_armour"));

        // Start battle
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);

        BattleResponse battle = res.getBattles().get(0);
        RoundResponse firstRound = battle.getRounds().get(0);

        // Player attack value (without midnight armour buff)
        double playerBaseAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("player_attack", config));

        // Midnight armour attack buff value
        double armourAttack = Double.parseDouble(TestUtils.getValueFromConfigFile("midnight_armour_attack", config));

        assertEquals((playerBaseAttack + armourAttack) / 5, -firstRound.getDeltaEnemyHealth(), 0.001);

        // Enemy attack value
        int enemyAttack = Integer.parseInt(TestUtils.getValueFromConfigFile("zombie_attack", config));

        // Ally defence buff value
        double armourDefence = Integer.parseInt(TestUtils.getValueFromConfigFile("midnight_armour_defence", config));

        assertEquals((enemyAttack - armourDefence) / 10, -firstRound.getDeltaCharacterHealth(), 0.001);
    }
}
