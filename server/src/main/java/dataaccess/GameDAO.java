package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.Game;
import model.JoinGameRequest;

import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDAO {

    private final Map<Integer, Game> gameDb = new HashMap<>();
    private static GameDAO instance = null;

    private final AuthDAO authDAO = AuthDAO.getInstance();

    private int createdGameID = 10;

    private GameDAO() {
    }

    public static GameDAO getInstance() {
        if (instance == null) {
            instance = new GameDAO();
        }
        return instance;
    }

    Gson gson = new Gson();

//    public void deleteAll() {
//        gameDb.clear();
//    }

    public void deleteAll() {
        String query = "DELETE FROM chess_games";

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public boolean gameNameAlreadyInUse(String gameName) {
        return gameDb.containsValue(gameName);
    }

//    public int createGame(String gameName, String authToken) {
//        if (!authDAO.isValidToken(authToken)) {
//            throw new RuntimeException("Invalid AuthToken");
//        }
//        int id = createdGameID;
//        gameDb.put(id, new Game(id, gameName, null, null, new ChessGame()));
//        createdGameID++;
//        return id;
//    }

    public int createGame(String gameName, String authToken) {
        if (!authDAO.isValidToken(authToken)) {
            throw new RuntimeException("Invalid AuthToken");
        }

        int id = createdGameID;
        String query = "INSERT INTO chess_games (gameID, gameName, whiteUserName, blackUserName, chess_board) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, id);
            stmt.setString(2, gameName);
            stmt.setString(3, null);
            stmt.setString(4, null);
            stmt.setString(5, gson.toJson(new Game(id, gameName, null, null, new ChessGame())));

            stmt.executeUpdate();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        createdGameID++;
        return id;
    }

//    public boolean isValidGameID(Integer gameID) {
//        if (gameID == null) {
//            return false;
//        }
//        return gameDb.containsKey(gameID);
//    }

    public boolean isValidGameID(Integer gameID) {
        if (gameID == null) {
            return false;
        }
        String query = "SELECT COUNT(*) FROM chess_games WHERE gameID = ?";

        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1)>0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }


    }

    public boolean isValidColor(String playerColor) {
        if (playerColor == null) {
            return false;
        }

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            return false;
        }
        return !playerColor.equals(null);
    }


    public boolean isStealingTeamColor(JoinGameRequest req) {
        String playerColor = req.playerColor();

        if ("BLACK".equals(playerColor) && Objects.equals(gameDb.get(req.gameID()).blackUsername(), null)) {
            return true;
        } else {
            return playerColor.equals("WHITE") && Objects.equals(gameDb.get(req.gameID()).whiteUsername(), null);
        }
    }


//    public void addUsername(JoinGameRequest req, String name) {
//        Game gameData = gameDb.get(req.gameID());
//        if (gameData != null) {
//            Game updatedGame;
//            if (req.playerColor().equals("WHITE")) {
//                updatedGame = new Game(gameData.gameID(),gameData.gameName(), name, gameData.blackUsername() ,gameData.game());
//            } else {
//                updatedGame = new Game(gameData.gameID(),gameData.gameName(), gameData.whiteUsername(), name, gameData.game());
//            }
//            gameDb.put(req.gameID(), updatedGame);
//        }
//    }

    public void addUsername(JoinGameRequest req, String name) {
        String query;
        if (req.playerColor().equals("WHITE")) {
            query = "UPDATE chess_games SET whiteUserName = ? WHERE gameID = ?";
        } else {
            query = "UPDATE chess_games SET blackUserName = ? WHERE gameID = ?";
        }
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, name);
            stmt.setInt(2, req.gameID());
            stmt.executeUpdate();

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



//    public ArrayList<Game> listGames() {
//        ArrayList<Game> gamesList = new ArrayList<>();
//
//        for (Game game : gameDb.values()) {
//            gamesList.add(new Game(game.gameID(), game.gameName(), game.whiteUsername(), game.blackUsername(),null));
//        }
//        return gamesList;
//    }


    public ArrayList<Game> listGames() {

        ArrayList<Game> gamesList = new ArrayList<>();
        String query = "SELECT gameID, gameName, whiteUserName, blackUserName, chess_board FROM chess_games";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(query);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                gamesList.add(readGame(rs));
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }

        return gamesList;
    }

    private Game readGame(ResultSet rs) throws SQLException {
        int gameId = rs.getInt("gameID");
        String gameName = rs.getString("gameName");
        String whiteUserName = rs.getString("whiteUserName");
        String blackUserName = rs.getString("blackUserName");

        String chessBoardJson = rs.getString("chess_board");
        ChessGame chessBoard = new Gson().fromJson(chessBoardJson, ChessGame.class);

        return new Game(gameId, gameName, whiteUserName, blackUserName, chessBoard);
    }
}