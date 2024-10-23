package server.handlers;
import com.google.gson.Gson;
import server.services.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler {

    public record LoginResponse(boolean success, String message){}
    private final LoginService loginService = new LoginService();

    public String handle (Request req, Response res){
    String message = loginService.login(req);

    LoginHandler.LoginResponse response;
    if (message.startsWith("Logged in! Token :")){
        res.status(200);
        response = new LoginHandler.LoginResponse(true, "Successful Login");
    } else if (message.equals("Incorrect Password")) {
        res.status(401);
        response = new LoginHandler.LoginResponse(false, "Error: unauthorized");
    } else if (message.equals("Account does not exist")) {
        res.status(401);
        response = new LoginHandler.LoginResponse(false, "Error: Account does not exist");
    } else if (message.equals("Error")) {
        res.status(500);
        response = new LoginHandler.LoginResponse(false, "Error");
    }
    else {response = new LoginHandler.LoginResponse(false, "Failed in Handler");
    }

    Gson gson = new Gson();
    return gson.toJson(response);
    }
}
