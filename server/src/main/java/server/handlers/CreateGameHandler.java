package server.handlers;

import com.google.gson.Gson;
import model.CreateGameRequest;
import model.RegisterRequest;
import server.services.CreateGameService;
import spark.Request;
import spark.Response;
import model.CreateGameResponse;

public class CreateGameHandler {
    private final CreateGameService CreateGameService = new CreateGameService();

    public String handle (Request req, Response res){
        Gson gson = new Gson();
        String authToken = req.headers("authorization");
        CreateGameRequest createGameRequest = gson.fromJson(req.body(), CreateGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new CreateGameResponse(false, "Error: unauthorized", null));
        }

        CreateGameResponse response = CreateGameService.createGame(createGameRequest);

        if (response.success() ){
            res.status(200);
        }
        else if (response.message().equals("Error: bad request")){
            res.status(400);
        }
        else if (response.message().equals("Error: unauthorized")){
            res.status(401);
        }
        else if (response.message().equals("Error: Name already in use")){
            res.status(403);
        }
        else if (response.message().equals("Error")){
            res.status(500);
        }
        return gson.toJson(response);
    }
}
