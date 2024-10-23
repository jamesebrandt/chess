package server.handlers;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import server.services.RegisterService;

public class RegisterHandler {

    public record RegisterResponse(Boolean success, String message) {
    }

    public String handle (Request req, Response res) {
        RegisterService regService = new RegisterService();
        boolean success = regService.register(req);

        RegisterHandler.RegisterResponse response;
        if (success) {
            res.status(200);
            response = new RegisterHandler.RegisterResponse(true, "Successful Registration");
        } else {
            res.status(500);
            response = new RegisterHandler.RegisterResponse(false, "Failed to Register.");
        }

        Gson gson = new Gson();
        return gson.toJson(response);


    }
}