package server;
import server.handlers.RegisterHandler;
import spark.Spark;
import static spark.Spark.*;
import java.util.UUID;

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





        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}

