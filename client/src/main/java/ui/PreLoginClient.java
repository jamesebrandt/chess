package ui;

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

    public String register(String... input){
        return "not implemented";
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
