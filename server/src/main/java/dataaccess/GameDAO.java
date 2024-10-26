package dataaccess;

import model.Game;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class GameDAO {

    private final Map<Integer, Game> gameDb = new HashMap<>();
    private static GameDAO instance = null;

    private int createdGameID = 10;

    private GameDAO() {}
    public static GameDAO getInstance() {
        if (instance == null) {
            instance = new GameDAO();
        }
        return instance;
    }

    public void deleteAll() {
        gameDb.clear();
    }

    public Game getGame(int gameID) {
        return gameDb.get(gameID);
    }

    public boolean isDuplicateGameName(String gameName) {
        return gameDb.values().stream()
                .anyMatch(game -> game.gameName().equals(gameName));
    }

    public int createGame(String gameName, String authToken) {
        int id = createdGameID;
        gameDb.put(id, new Game(id, gameName, "", "", authToken));
        createdGameID++;
        return id;
    }

    public Map<String, List<Map<String, Object>>> listGames() {
        List<Map<String, Object>> gamesList = new ArrayList<>();
        for (Game game : gameDb.values()) {
            Map<String, Object> gameInfo = new HashMap<>();
            gameInfo.put("gameID", game.gameID());
            gameInfo.put("whiteUsername", game.whiteUsername());
            gameInfo.put("blackUsername", game.blackUsername());
            gameInfo.put("gameName", game.gameName());
            gamesList.add(gameInfo);
        }

        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("games", gamesList);
        return result;
    }
}