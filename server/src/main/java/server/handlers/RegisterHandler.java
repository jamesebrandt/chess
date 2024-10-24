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

        return gson.toJson(response);
    }
}