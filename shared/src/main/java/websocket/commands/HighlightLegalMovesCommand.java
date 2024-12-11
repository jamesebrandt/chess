package websocket.commands;

public class HighlightLegalMovesCommand extends UserGameCommand {

    private final String pieceID;

    public HighlightLegalMovesCommand(String authToken, Integer gameID, String pieceID) {
        super(CommandType.HIGHLIGHT_LEGAL_MOVES, authToken, gameID);
        this.pieceID = pieceID;
    }

    public String getPieceID() {
        return pieceID;
    }
}
