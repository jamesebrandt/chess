package server.handlers;
import com.google.gson.Gson;
import server.services.LoginService;
import model.LoginResponse;
import model.LoginRequest;
import spark.Request;
import spark.Response;

public class LoginHandler {

    private final LoginService loginService = new LoginService();

    public String handle (Request req, Response res){
        Gson gson = new Gson();

        LoginRequest loginRequest = gson.fromJson(req.body(), LoginRequest.class);

        String message = loginService.login(loginRequest);

        LoginResponse response;
        if (message.startsWith("Logged in! Token:")){
            res.status(200);
            response = new LoginResponse(true, "Successful Login");
        } else if (message.equals("Incorrect Password")) {
            res.status(401);
            response = new LoginResponse(false, "Error: unauthorized");
        } else if (message.equals("Account does not exist")) {
            res.status(401);
            response = new LoginResponse(false, "Error: Account does not exist");
        } else if (message.equals("Error")) {
            res.status(500);
            response = new LoginResponse(false, "Error");
        }
        else {response = new LoginResponse(false, "Failed in Handler");
        }

        return gson.toJson(response);
    }
}
