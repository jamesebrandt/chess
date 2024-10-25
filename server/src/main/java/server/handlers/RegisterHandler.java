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
        RegisterResponse response = registerService.register(registerRequest);

        if (response.success()){
            res.status(200);
        }
        else if (response.message().equals("Error: bad request")){
            res.status(400);
        }
        else if (response.message().equals("Error: already taken")){
            res.status(403);
        }
        else if (response.message().equals("Error")){
            res.status(500);
        }
        return gson.toJson(response);
    }
}