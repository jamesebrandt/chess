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

    public void deleteAll() {
        String query = "TRUNCATE TABLE chess_games";

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public boolean gameNameAlreadyInUse(String tryGameName) {

        if (tryGameName == null) {
            return false;
        }

        String query = "SELECT COUNT(*) FROM chess_games WHERE gameName = ?";

        try{
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tryGameName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1)>0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
        String colorColumn = req.playerColor().equals("BLACK") ? "blackUserName" : "whiteUserName";
        String query = "SELECT COUNT(*) FROM chess_games WHERE gameID = ? AND " + colorColumn + " IS NULL";

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, req.gameID());
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

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
            throw new ResponseException(String.format("Unable to read data: %s", e.getMessage()));
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