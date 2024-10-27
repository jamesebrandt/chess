package server;
import server.handlers.*;
import spark.Spark;
import static spark.Spark.*;


public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/web");

        // Register your endpoints and handle exceptions here.

        //clear
        delete("/db", (req, res) -> {
            ClearHandler clearHandler = new ClearHandler();
            return clearHandler.handle(req, res);
        });

        //register
        post("/user", (req, res) ->{
            RegisterHandler registerHandler = new RegisterHandler();
            return registerHandler.handle(req, res);
        });

        //login
        post("/session", (req, res) ->{
            LoginHandler loginHandler = new LoginHandler();
            return loginHandler.handle(req, res);
        });

        // logout
        path("/session", () -> {
            delete("", new LogoutHandler()::handle);
        });

        //game
        post("/game", (req, res) -> {
            CreateGameHandler createGameHandler = new CreateGameHandler();
            return createGameHandler.handle(req, res);
        });

        get("/game", (req, res) -> {
            GameListHandler gameListHandler = new GameListHandler();
            return gameListHandler.handle(req, res);
        });

        put("/game", (req, res) -> {
           JoinGameHandler joinGameHandler = new JoinGameHandler();
           return joinGameHandler.handle(req, res);
        });








        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

