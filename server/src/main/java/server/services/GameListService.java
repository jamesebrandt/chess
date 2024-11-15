package server.services;

import model.Game;
import model.GameListResponse;
import model.GameListRequest;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;

import java.util.ArrayList;

public class GameListService {

    private final GameDAO gameDAO = GameDAO.getInstance();
    private final AuthDAO authDAO = AuthDAO.getInstance();

    public GameListResponse listGames(GameListRequest request) {
        try {
            if (!authDAO.isValidToken(request.authToken())) {
                return new GameListResponse(false, "Error: unauthorized", null);
            } else {
                ArrayList<Game> gamesList = gameDAO.listGames();

                return new GameListResponse(true, "Success", gamesList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new GameListResponse(false, "Error: " + e.getMessage(), null);
        }
    }
}