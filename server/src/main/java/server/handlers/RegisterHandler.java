package server.handlers;
import com.google.gson.Gson;
import model.RegisterRequest;
import model.RegisterResponse;
import spark.Request;
import spark.Response;
import server.services.RegisterService;


public class RegisterHandler {

    private final RegisterService registerService = new RegisterService();

    public String handle (Request req, Response res) {
        Gson gson = new Gson();

        RegisterRequest registerRequest = gson.fromJson(req.body(), RegisterRequest.class);

        String message = registerService.register(registerRequest);

        RegisterResponse response;
        if (message.startsWith("Successful Registration! Token:")) {
            res.status(200);
            response = new RegisterResponse(true, "Successful Registration");
        } else if(message.equals("Username is Taken")) {
            res.status(403);
            response = new RegisterResponse(false, "Username Taken");
        }
        else if (message.equals("Error")){
            res.status(500);
            response = new RegisterResponse(false, "ERROR with Registration");
        }
        else if (message.equals("Bad Request")){
            res.status(400);
            response = new RegisterResponse(false, "Bad Request");
        }
        else { response = new RegisterResponse(false, "Failed at Handler");}

        return gson.toJson(response);
    }
}