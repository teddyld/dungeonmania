package dungeonmania;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.File;

import org.json.JSONException;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

public class DungeonManiaController {
    private Game game = null;

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException(dungeonName + " is not a dungeon that exists");
        }

        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }

        try {
            GameBuilder builder = new GameBuilder();
            game = builder.setConfigName(configName).setDungeonName(dungeonName).buildGame();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return null;
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.tick(itemUsedId));
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        return ResponseBuilder.getDungeonResponse(game.tick(movementDirection));
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        List<String> validBuildables = List.of("bow", "shield", "midnight_armour", "sceptre");
        if (!validBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Only bow, shield, midnight_armour and sceptre can be built");
        }

        return ResponseBuilder.getDungeonResponse(game.build(buildable));
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        return ResponseBuilder.getDungeonResponse(game.interact(entityId));
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) {
        try {
            File location = new File("build/saves");
            location.mkdirs();
            File newFile = new File("build/saves/" + name);
            FileOutputStream fos = new FileOutputStream(newFile, false);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(game);
            oos.flush();
            oos.close();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.toString());

        }
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        try {
            FileInputStream fis = new FileInputStream("build/saves/" + name);
            ObjectInputStream ois = new ObjectInputStream(fis);
            game = (Game) ois.readObject();
            ois.close();
            return ResponseBuilder.getDungeonResponse(game);
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalArgumentException(e.toString());
        }
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        File dir = new File("build/saves");
        return (dir.isDirectory())
            ? Stream.of(dir.listFiles())
            .map(File::getName)
            .collect(Collectors.toList())
            : new ArrayList<>();
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(
            int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException {
        return null;
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        return null;
    }

}
