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

        LoginResponse response = loginService.login(loginRequest);

        return gson.toJson(response);
    }
}
