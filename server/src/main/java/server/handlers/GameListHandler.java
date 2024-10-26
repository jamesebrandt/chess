package server.handlers;

import com.google.gson.Gson;
import model.GameListRequest;
import model.GameListResponse;
import server.services.GameListService;
import spark.Request;
import spark.Response;

public class GameListHandler {
    private final GameListService gameListService = new GameListService();
    private final Gson gson = new Gson();

    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new GameListResponse(false, "Error: unauthorized", null));
        }

        GameListResponse response = gameListService.listGames(new GameListRequest(authToken));

        if (response.success()) {
            res.status(200);
        } else if (response.message().equals("Error: unauthorized")) {
            res.status(401);
        } else {
            res.status(500);
        }

        res.type("application/json");
        return gson.toJson(response);
    }
}