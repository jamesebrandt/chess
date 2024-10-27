package dataaccess;

import model.Game;
import model.JoinGameRequest;
import model.PlayerColor;

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
        if (!gameDb.containsKey(gameID)) {
            throw new IllegalArgumentException("Game ID not found: " + gameID);
        }
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

    public boolean isValidGameID(int gameID){
        return gameDb.containsKey(gameID);
    }

    public boolean canJoinGameDb(JoinGameRequest req){
        if (!isValidGameID(req.gameID())){
            return false;
        }
        if (req.color() == PlayerColor.BLACK && gameDb.get(req.gameID()).blackUsername() == null){
            return true;
        }
        else return req.color() == PlayerColor.WHITE && gameDb.get(req.gameID()).whiteUsername() == null;
    }

    public void addUsername(JoinGameRequest req, String name) {
        Game gameData = gameDb.get(req.gameID());
        if (gameData != null) {
            Game updatedGame;
            if (req.color().equals(PlayerColor.WHITE)) {
                updatedGame = new Game(gameData.gameID(), gameData.gameName(), name, gameData.blackUsername(), gameData.authToken());
            } else {
                updatedGame = new Game(gameData.gameID(), gameData.gameName(), gameData.whiteUsername(), name, gameData.authToken());
            }
            gameDb.put(req.gameID(), updatedGame);
        }
    }

    public Map<String, List<Map<String, Object>>> listGames() {
        List<Map<String, Object>> gamesList = new ArrayList<>();
        for (Game game : gameDb.values()) {
            Map<String, Object> gameInfo = Map.of(
                    "gameID", game.gameID(),
                    "whiteUsername", game.whiteUsername(),
                    "blackUsername", game.blackUsername(),
                    "gameName", game.gameName()
            );
            gamesList.add(gameInfo);
        }
        return Map.of("games", gamesList);
    }
}