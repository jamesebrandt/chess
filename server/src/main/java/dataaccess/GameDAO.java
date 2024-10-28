package dataaccess;

import model.Game;
import model.JoinGameRequest;

import java.util.*;

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
        gameDb.put(id, new Game(id, gameName, null, null, authToken));
        createdGameID++;
        return id;
    }

    public boolean isValidGameID(int gameID){
        if (gameDb.containsKey(gameID)){
            return true;
        }
        return false;
    }


    public boolean isStealingTeamColor(JoinGameRequest req){
        String playerColor = req.playerColor();
        if (playerColor == null) {
            return false;
        }
        if ("BLACK".equals(playerColor) && Objects.equals(gameDb.get(req.gameID()).blackUsername(), null)){
            return true;
        }
        else return playerColor.equals("WHITE") && Objects.equals(gameDb.get(req.gameID()).whiteUsername(), null);
    }


    public void addUsername(JoinGameRequest req, String name) {
        Game gameData = gameDb.get(req.gameID());
        if (gameData != null) {
            Game updatedGame;
            if (req.playerColor().equals("WHITE")) {
                updatedGame = new Game(gameData.gameID(), gameData.gameName(), name, gameData.whiteUsername(), gameData.authToken());
            } else {
                updatedGame = new Game(gameData.gameID(), gameData.gameName(), gameData.blackUsername(), name, gameData.authToken());
            }
            gameDb.put(req.gameID(), updatedGame);
        }
    }

    public Map<String, List<Map<String, Object>>> listGames() {
        List<Map<String, Object>> gamesList = new ArrayList<>();
        for (Game game : gameDb.values()) {
            Map<String, Object> gameInfo = Map.of(
                    "gameID", game.gameID(),
                    "whiteUsername", game.whiteUsername()== null ? "null": game.whiteUsername(),
                    "blackUsername", game.blackUsername()== null ? "null": game.blackUsername(),
                    "gameName", game.gameName()
            );
            gamesList.add(gameInfo);
        }
        return Map.of("games", gamesList);
    }
}