package ui;

import model.RegisterRequest;
import model.RegisterResponse;

import java.text.MessageFormat;
import java.util.Arrays;

public class PreLoginClient {

    private final ServerFacade server;

    public PreLoginClient(String serverUrl){
        server = new ServerFacade(serverUrl);
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
            RegisterResponse registerResponse =  server.register(input[0], input[1], input[2]);
            if (registerResponse.success()) {
                return "User Registered and logged in under "+ registerResponse.username();
            }
            else{
                return registerResponse.message();
            }
        }
        throw new RuntimeException("Expected: <username> <password> <email>");
    }

    public String login(String... input){


        return "Successful Login";
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
