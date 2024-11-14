package ui;

import model.LoginResponse;
import model.RegisterResponse;
import java.util.Arrays;

public class PreLoginClient {

    private final ServerFacade serverFacade;
    private SessionManager manager = new SessionManager();

    public PreLoginClient(String serverUrl){
        this.serverFacade = ServerFacade.getInstance(serverUrl);
    }

    public String eval(String inputLine){
        try {
            var tokens = inputLine.toUpperCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "REGISTER" -> register(params);
                case "LOGIN" -> login(params);
                case "QUIT" -> "Quitting Client";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String register(String... input) throws Exception {
        if (input.length >= 3){
            RegisterResponse registerResponse = serverFacade.register(input[0], input[1], input[2]);
            if (registerResponse.success()) {
                return "User Registered and logged in under "+ registerResponse.username();
            }
            else{
                return registerResponse.message();
            }
        }
        throw new RuntimeException("Expected: <username> <password> <email>");
    }

    public String login(String... input) throws Exception {
        if (input.length >= 2){
            LoginResponse loginResponse = serverFacade.login(input[0], input[1]);
            if (loginResponse.success()) {
                String authToken = loginResponse.authToken();
                manager.addSessionToken(loginResponse.username(), authToken);
                return "User Registered and logged in under "+ loginResponse.username();
            }
            else{
                return "Login Failed: " + loginResponse.message();
            }
        }
        throw new RuntimeException("Expected: <username> <password>");
    }

    public String help() {
        return """
                - Register <username> <password> <email>
                - Login <username> <password>
                - Help
                - Quit
                """;
    }
}
