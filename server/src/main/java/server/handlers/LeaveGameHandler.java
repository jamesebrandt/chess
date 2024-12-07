package server.handlers;

import com.google.gson.Gson;
import model.JoinGameRequest;
import model.JoinGameResponse;
import server.services.LeaveGameService;
import spark.Request;
import spark.Response;

public class LeaveGameHandler {

    private final LeaveGameService leaveGameService = new LeaveGameService();

    public String handle(Request req, Response res){
        var gson = new Gson();
        var authToken = req.headers("authorization");

        JoinGameRequest joinGameRequest = gson.fromJson(req.body(), JoinGameRequest.class);

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new JoinGameResponse(false, "Error: unauthorized"));
        }

        JoinGameResponse response = leaveGameService.leaveGame(joinGameRequest, authToken);
        if (response.success()){
            res.status(200);
        } else if(response.message().equals("Error: unauthorized to leave")) {
            res.status(401);
        } else if(response.message().equals("Error: bad request")) {
            res.status(400);
        }else{
            res.status(500);
        }

        return gson.toJson(response);
    }
}
