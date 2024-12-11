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

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, tryGameName);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

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

        int id = generateGameId();
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
        return id;
    }


    public boolean isValidGameID(Integer gameID) {
        if (gameID == null) {
            return false;
        }
        String query = "SELECT COUNT(*) FROM chess_games WHERE gameID = ?";

        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;

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

    public void removeUser(JoinGameRequest req) {
        String query;
        if (req.playerColor().equals("WHITE")) {
            query = "UPDATE chess_games SET whiteUserName = NULL WHERE gameID = ?";
        } else {
            query = "UPDATE chess_games SET blackUserName = NULL WHERE gameID = ?";
        }
        try {
            Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, String.valueOf(req.gameID()));
            stmt.executeUpdate();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getTeamColor(String username) {
        String query = "SELECT CASE " +
                "WHEN whiteUserName = ? THEN 'WHITE' " +
                "WHEN blackUserName = ? THEN 'BLACK' " +
                "ELSE NULL END AS teamColor " +
                "FROM chess_games " +
                "WHERE whiteUserName = ? OR blackUserName = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set the username parameter for all placeholders
            stmt.setString(1, username);
            stmt.setString(2, username);
            stmt.setString(3, username);
            stmt.setString(4, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("teamColor");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to get team color for username: " + username, e);
        }

        return null;
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

    public Game getGame(int gameID) {
        String query = "SELECT gameID, gameName, whiteUserName, blackUserName, chess_board FROM chess_games WHERE gameID = ?";

        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(query)) {

            // Set the gameID parameter
            ps.setInt(1, gameID);

            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Call a helper method to map the result set to a Game object
                    return readGame(rs);
                } else {
                    throw new ResponseException(String.format("Game with ID %d not found.", gameID));
                }
            }

        } catch (Exception e) {
            throw new ResponseException(String.format("Unable to read data: %s", e.getMessage()));
        }
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

    private Integer generateGameId() {
        Random random = new Random();
        Integer randomNumber = 10000000 + random.nextInt(90000000); // Range: 10,000,000 to 99,999,999
        return randomNumber;
    }


}