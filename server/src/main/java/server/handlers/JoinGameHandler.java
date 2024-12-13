package server.handlers;

import com.google.gson.Gson;
import model.*;
import server.services.JoinGameService;
import spark.Response;
import spark.Request;

public class JoinGameHandler {

    private final JoinGameService joinGameService = new JoinGameService();

    public String handle(Request req, Response res){
        Gson gson = new Gson();
        String authToken = req.headers("authorization");

        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new JoinGameResponse(false, "Error: unauthorized"));
        }

        JoinGameResponse response = joinGameService.joinGame(joinGameRequest, authToken);
        if (response.success()){
            res.status(200);
        } else if(response.message().equals("Error: unauthorized")) {
            res.status(401);
        } else if(response.message().equals("Error: bad request")) {
            res.status(400);
        } else if(response.message().equals("Error: already taken")) {
            res.status(403);
        }else{
            res.status(500);
        }

        return gson.toJson(response);
    }
}
