package websocket.commands;

public class RedrawBoardCommand extends UserGameCommand {

    public RedrawBoardCommand(String authToken, Integer gameID) {
        super(CommandType.REDRAW_CHESS_BOARD, authToken, gameID);
    }
}
