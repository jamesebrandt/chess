package server;
import spark.*;
import java.util.UUID;


public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        post("/clear", (req, res) -> {
            ClearHandler clearHandler = new ClearHandler();
            return clearHandler.handle(req, res);
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

