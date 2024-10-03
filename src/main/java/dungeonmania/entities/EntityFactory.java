package dungeonmania.entities;

import dungeonmania.Game;
import dungeonmania.entities.buildables.*;
import dungeonmania.entities.collectables.*;
import dungeonmania.entities.enemies.*;
import dungeonmania.map.GameMap;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.io.Serializable;

import org.json.JSONObject;

public class EntityFactory implements Serializable {
    private String configStr;
    private Random ranGen = new Random();

    public EntityFactory(JSONObject config) {
        this.configStr = config.toString();
    }

    public Entity createEntity(JSONObject jsonEntity) {
        JSONObject config = new JSONObject(configStr);
        return constructEntity(jsonEntity, config);
    }

    public void spawnSpider(Game game) {
        JSONObject config = new JSONObject(configStr);
        GameMap map = game.getMap();
        int tick = game.getTick();
        int rate = config.optInt("spider_spawn_interval", 0);
        if (rate == 0 || (tick + 1) % rate != 0) return;
        int radius = 20;
        Position player = map.getPlayerPosition();

        Spider dummySpider = buildSpider(new Position(0, 0)); // for checking possible positions

        List<Position> availablePos = new ArrayList<>();
        for (int i = player.getX() - radius; i < player.getX() + radius; i++) {
            for (int j = player.getY() - radius; j < player.getY() + radius; j++) {
                if (Position.calculatePositionBetween(player, new Position(i, j)).magnitude() > radius) continue;
                Position np = new Position(i, j);
                if (!map.canMoveTo(dummySpider, np)) continue;
                availablePos.add(np);
            }
        }
        Position initPosition = availablePos.get(ranGen.nextInt(availablePos.size()));
        Spider spider = buildSpider(initPosition);
        map.addEntity(spider);
        game.register((Runnable & Serializable) () -> spider.move(game), Game.AI_MOVEMENT, spider.getId());
    }

    public void spawnZombie(Game game, ZombieToastSpawner spawner) {
        JSONObject config = new JSONObject(configStr);
        GameMap map = game.getMap();
        int tick = game.getTick();
        Random randGen = new Random();
        int spawnInterval = config.optInt("zombie_spawn_interval", ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL);
        if (spawnInterval == 0 || (tick + 1) % spawnInterval != 0) return;
        List<Position> pos = spawner.getCardinallyAdjacentPositions();
        pos = pos
            .stream()
            .filter(p -> !map.getEntities(p).stream().anyMatch(e -> (e instanceof Wall)))
            .collect(Collectors.toList());
        if (pos.size() == 0) return;
        ZombieToast zt = buildZombieToast(pos.get(randGen.nextInt(pos.size())));
        map.addEntity(zt);
        game.register((Runnable & Serializable) () -> zt.move(game), Game.AI_MOVEMENT, zt.getId());
    }

    public Spider buildSpider(Position pos) {
        JSONObject config = new JSONObject(configStr);
        double spiderHealth = config.optDouble("spider_health", Spider.DEFAULT_HEALTH);
        double spiderAttack = config.optDouble("spider_attack", Spider.DEFAULT_ATTACK);
        return new Spider(pos, spiderHealth, spiderAttack);
    }

    public Player buildPlayer(Position pos) {
        JSONObject config = new JSONObject(configStr);
        double playerHealth = config.optDouble("player_health", Player.DEFAULT_HEALTH);
        double playerAttack = config.optDouble("player_attack", Player.DEFAULT_ATTACK);
        double allyAttack = config.optDouble("ally_attack", Player.DEFAULT_ALLY_ATTACK);
        double allyDefence = config.optDouble("ally_defence", Player.DEFAULT_ALLY_DEFENCE);
        return new Player(pos, playerHealth, playerAttack, allyAttack, allyDefence);
    }

    public ZombieToast buildZombieToast(Position pos) {
        JSONObject config = new JSONObject(configStr);
        double zombieHealth = config.optDouble("zombie_health", ZombieToast.DEFAULT_HEALTH);
        double zombieAttack = config.optDouble("zombie_attack", ZombieToast.DEFAULT_ATTACK);
        return new ZombieToast(pos, zombieHealth, zombieAttack);
    }

    public ZombieToastSpawner buildZombieToastSpawner(Position pos) {
        JSONObject config = new JSONObject(configStr);
        int zombieSpawnRate = config.optInt("zombie_spawn_interval", ZombieToastSpawner.DEFAULT_SPAWN_INTERVAL);
        return new ZombieToastSpawner(pos, zombieSpawnRate);
    }

    public Mercenary buildMercenary(Position pos) {
        JSONObject config = new JSONObject(configStr);
        double mercenaryHealth = config.optDouble("mercenary_health", Mercenary.DEFAULT_HEALTH);
        double mercenaryAttack = config.optDouble("mercenary_attack", Mercenary.DEFAULT_ATTACK);
        int mercenaryBribeAmount = config.optInt("bribe_amount", Mercenary.DEFAULT_BRIBE_AMOUNT);
        int mercenaryBribeRadius = config.optInt("bribe_radius", Mercenary.DEFAULT_BRIBE_RADIUS);
        return new Mercenary(pos, mercenaryHealth, mercenaryAttack, mercenaryBribeAmount, mercenaryBribeRadius);
    }

    public Assassin buildAssassin(Position pos) {
        JSONObject config = new JSONObject(configStr);
        double assassinHealth = config.optDouble("assassin_attack", Assassin.DEFAULT_HEALTH);
        double assassinAttack = config.optDouble("assassin_attack", Assassin.DEFAULT_ATTACK);
        int assassinBribeAmount = config.optInt("assassin_bribe_amount", Assassin.DEFAULT_BRIBE_AMOUNT);
        int assassinBribeRadius = config.optInt("bribe_radius", Assassin.DEFAULT_BRIBE_RADIUS);
        double assassinBribeFailRate = config.optDouble("assassin_bribe_fail_rate", Assassin.DEFAULT_FAIL_RATE);
        return new Assassin(pos, assassinHealth, assassinAttack,
        assassinBribeAmount, assassinBribeRadius, assassinBribeFailRate);
    }

    public Bow buildBow() {
        JSONObject config = new JSONObject(configStr);
        int bowDurability = config.optInt("bow_durability");
        return new Bow(bowDurability);
    }

    public Shield buildShield() {
        JSONObject config = new JSONObject(configStr);
        int shieldDurability = config.optInt("shield_durability");
        double shieldDefence = config.optInt("shield_defence");
        return new Shield(shieldDurability, shieldDefence);
    }

    public MidnightArmour buildMidnightArmour() {
        JSONObject config = new JSONObject(configStr);
        double armourAttack = config.optDouble("midnight_armour_attack", MidnightArmour.DEFAULT_ATTACK);
        double armourDefence = config.optDouble("midnight_armour_defence", MidnightArmour.DEFAULT_DEFENCE);
        return new MidnightArmour(armourAttack, armourDefence);
    }

    public Sceptre buildSceptre() {
        JSONObject config = new JSONObject(configStr);
        int mindControlDuration = config.optInt("mind_control_duration", Sceptre.DEFAULT_DURATION);
        return new Sceptre(mindControlDuration);
    }

    private Entity constructEntity(JSONObject jsonEntity, JSONObject config) {
        Position pos = new Position(jsonEntity.getInt("x"), jsonEntity.getInt("y"));

        switch (jsonEntity.getString("type")) {
        case "player":
            return buildPlayer(pos);
        case "zombie_toast":
            return buildZombieToast(pos);
        case "zombie_toast_spawner":
            return buildZombieToastSpawner(pos);
        case "mercenary":
            return buildMercenary(pos);
        case "assassin":
            return buildAssassin(pos);
        case "wall":
            return new Wall(pos);
        case "boulder":
            return new Boulder(pos);
        case "switch":
            return new Switch(pos);
        case "exit":
            return new Exit(pos);
        case "treasure":
            return new Treasure(pos);
        case "wood":
            return new Wood(pos);
        case "arrow":
            return new Arrow(pos);
        case "bomb":
            int bombRadius = config.optInt("bomb_radius", Bomb.DEFAULT_RADIUS);
            return new Bomb(pos, bombRadius);
        case "invisibility_potion":
            int invisibilityPotionDuration = config.optInt(
                "invisibility_potion_duration",
                InvisibilityPotion.DEFAULT_DURATION);
            return new InvisibilityPotion(pos, invisibilityPotionDuration);
        case "invincibility_potion":
            int invincibilityPotionDuration = config.optInt("invincibility_potion_duration",
            InvincibilityPotion.DEFAULT_DURATION);
            return new InvincibilityPotion(pos, invincibilityPotionDuration);
        case "portal":
            return new Portal(pos, ColorCodedType.valueOf(jsonEntity.getString("colour")));
        case "sword":
            double swordAttack = config.optDouble("sword_attack", Sword.DEFAULT_ATTACK);
            int swordDurability = config.optInt("sword_durability", Sword.DEFAULT_DURABILITY);
            return new Sword(pos, swordAttack, swordDurability);
        case "spider":
            return buildSpider(pos);
        case "door":
            return new Door(pos, jsonEntity.getInt("key"));
        case "key":
            return new Key(pos, jsonEntity.getInt("key"));
        case "sun_stone":
            return new SunStone(pos);
        case "swamp_tile":
            int movementFactor = config.optInt("movement_factor", SwampTile.DEFAULT_MOVEMENT_FACTOR);
            return new SwampTile(pos, movementFactor);
        default:
            return null;
        }
    }

    public int getSwampMF() {
        JSONObject config = new JSONObject(configStr);
        return config.optInt("movement_factor", SwampTile.DEFAULT_MOVEMENT_FACTOR);
    }
}
