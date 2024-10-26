package server.services;

import model.Game;
import model.GameListResponse;
import model.GameListRequest;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import java.util.List;
import java.util.Map;

public class GameListService {

    private final GameDAO gameDAO = GameDAO.getInstance();
    private final AuthDAO authDAO = AuthDAO.getInstance();

    public GameListResponse listGames(GameListRequest request) {
        try {
            if (!authDAO.isValidToken(request.authToken())) {
                return new GameListResponse(false, "Error: unauthorized", null);
            } else {
                Map<String, List<Map<String, Object>>> gamesMap = gameDAO.listGames();
                List<Map<String, Object>> gamesList = gamesMap.get("games");
                return new GameListResponse(true, "Success", gamesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new GameListResponse(false, "Error: " + e.getMessage(), null);
        }
    }
}