package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;

    public MakeMoveCommand(String authToken, Integer gameID, ChessMove move) {
        super(CommandType.MAKE_MOVE, authToken, gameID, move);
        this.move = move;
    }

    public ChessMove getMove() {
        return move;
    }

    @Override
    public String toString() {
        return "MakeMoveCommand{" +
                "commandType=" + getCommandType() +
                ", authToken='" + getAuthToken() + '\'' +
                ", gameID=" + getGameID() +
                ", move=" + move +
                '}';
    }
}
