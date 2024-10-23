package server.handlers;
import server.services.LoginService;
import spark.Request;
import spark.Response;

public class LoginHandler {

    public record LoginResponse(boolean success, String message){}
    private final LoginService loginService = new LoginService();

    public String handle (Request req, Response res){
     String message = loginService.login(req);

     LoginHandler.LoginResponse response;




    }
}
