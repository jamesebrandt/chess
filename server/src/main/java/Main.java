import chess.*;
import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
    }

    public class simpleServer(){
        port(8080);

        get("hello", (req, res) -> "Hello World");


    }
}
