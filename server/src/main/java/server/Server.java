package server;
import server.handlers.RegisterHandler;
import server.handlers.LoginHandler;
import spark.Spark;
import static spark.Spark.*;

import server.handlers.ClearHandler;


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

        post("/session", (req, res) ->{
            LoginHandler loginHandler = new LoginHandler();
            return loginHandler.handle(req, res);
        });






        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}

