package server.handlers;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import server.services.RegisterService;

public class RegisterHandler {

    public record RegisterResponse(Boolean success, String message) {
    }
    private final RegisterService regService = new RegisterService();

    public String handle (Request req, Response res) {
        String message = regService.register(req);


        RegisterHandler.RegisterResponse response;
        if (message.startsWith("Successful Registration! Token:")) {
            res.status(201);
            response = new RegisterHandler.RegisterResponse(true, "Successful Registration");
        } else if(message.equals("Username is Taken")) {
            res.status(403);
            response = new RegisterHandler.RegisterResponse(false, "Username Taken");
        }
        else if (message.equals("Error")){
            res.status(500);
            response = new RegisterHandler.RegisterResponse(false, "ERROR with Registration");
        }
        else if (message.equals("Bad Request")){
            res.status(400);
            response = new RegisterHandler.RegisterResponse(false, "Bad Request");
        }
        else { response = new RegisterResponse(false, "Failed at Handler");}

        Gson gson = new Gson();
        return gson.toJson(response);
    }
}