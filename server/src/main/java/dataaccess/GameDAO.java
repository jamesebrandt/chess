package dataaccess;

import model.Game;
import java.util.HashMap;
import java.util.Map;

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

    public void deleteAll(){
        //String sql = "DELETE FROM Games";
    }

    public Game getGame(String gameID){
        return gameDb.get(gameID);
    }

    public boolean isDuplicateGameName(String gameName) {
        return gameDb.values().stream()
                .anyMatch(game -> game.gameName().equals(gameName));
    }


    public int createGame(String gameName, String authToken) {
        int id = createdGameID;
        gameDb.put(id, new Game(id, gameName, authToken));
        createdGameID++;
        return id;
    }


}
