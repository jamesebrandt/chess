package server.handlers;

import com.google.gson.Gson;
import model.LogoutResponse;
import server.services.LogoutService;
import spark.Request;
import spark.Response;

public class LogoutHandler {

    private final LogoutService logoutService = new LogoutService();

    public String handle(Request req, Response res){
        Gson gson = new Gson();
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new LogoutResponse(false, "Error: unauthorized"));
        }

        LogoutResponse response = logoutService.logout(authToken);

        if (response.success()) {
            res.status(200);
        } else if (response.message().equals("Error: unauthorized")) {
            res.status(401);
        } else {
            res.status(500);
        }

        return gson.toJson(response);

    }
}
