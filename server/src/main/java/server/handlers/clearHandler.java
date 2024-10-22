package server.handlers;

import spark.Request;
import spark.Response;
import server.services.ClearService;

public class ClearHandler {

    public String handle(Request req, Response res) {
        ClearService clearService = new ClearService();
        boolean success = clearService.clearAll();

        if (success) {
            res.status(200);  // HTTP 200 OK
            return "All data cleared successfully!";
        } else {
            res.status(500);  // HTTP 500 Internal Server Error
            return "Failed to clear data.";
        }
    }
}
