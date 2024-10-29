package dataaccess;

import chess.ChessGame;
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

    public boolean isDuplicateGameName(String gameName) {
        return gameDb.containsValue(gameName);
    }

    public int createGame(String gameName, String authToken) {
        int id = createdGameID;
        gameDb.put(id, new Game(id, gameName, null, null, new ChessGame()));
        createdGameID++;
        return id;
    }

    public boolean isValidGameID(Integer gameID){
        if (gameID == null){
            return false;
        }
        return gameDb.containsKey(gameID);
    }

    public boolean isValidColor(String playerColor){
        return playerColor != null;
    }


    public boolean isStealingTeamColor(JoinGameRequest req){
        String playerColor = req.playerColor();

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
                updatedGame = new Game(gameData.gameID(),gameData.gameName(), name, gameData.blackUsername() ,gameData.game());
            } else {
                updatedGame = new Game(gameData.gameID(),gameData.gameName(), gameData.whiteUsername(), name, gameData.game());
            }
            gameDb.put(req.gameID(), updatedGame);
        }
    }


    public ArrayList<Game> listGames() {
        ArrayList<Game> gamesList = new ArrayList<>();

        for (Game game : gameDb.values()) {
            gamesList.add(new Game(game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername(),null));
        }
        return gamesList;
    }
}