package chess;

import java.util.ArrayList;
import java.util.Collection;



/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamColor;

    public ChessGame() {
        board = new ChessBoard();
        board.resetBoard();
        teamColor = TeamColor.WHITE;
    }

    private ChessBoard CopyBoard(ChessBoard copy){
        ChessBoard boardCopy = new ChessBoard();
        for (int i = 0; i < copy.getBoard().length;i++){
            for (int j = 0; j < 9; j++){
                boardCopy.getBoard()[i][j] = copy.getBoard()[i][j];
            }
        }
        return boardCopy;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamColor;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (team == TeamColor.BLACK) {
            team = TeamColor.WHITE;
        }
        else {
            team = TeamColor.BLACK;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = getBoard().getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        } else {
            Collection<ChessMove> TestMoves = piece.pieceMoves(board, startPosition);
        }

        return null;

    }

    private boolean IsMovingIntoCheck(ChessMove move, ChessBoard board) {
        ChessPosition end = move.getEndPosition();

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                ChessPosition CheckingPosition = new ChessPosition(i, j);
                ChessPiece piece = getBoard().getPiece(CheckingPosition);
                if (piece == null) {
                    //skip
                } else if (piece.pieceMoves(board, CheckingPosition) == end) {
                    return true;
                }
            }
        }
        return false;
    }

    private ChessPosition FindKing(TeamColor team){
        ChessPiece King = new ChessPiece(team, ChessPiece.PieceType.KING);

        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                ChessPosition CheckingPosition = new ChessPosition(i, j);
                ChessPiece piece = getBoard().getPiece(CheckingPosition);
                if (piece == null) {
                    //skip
                } else if (piece == King) {
                    return CheckingPosition;
                }
            }
        }
        return null;
    }


    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition KingIsAt = FindKing(teamColor);
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                ChessPosition CheckingPosition = new ChessPosition(i, j);
                ChessPiece piece = getBoard().getPiece(CheckingPosition);

                if (piece == null) {
                    //skip
                } else if (teamColor != piece.getTeamColor() && piece.pieceMoves(board, CheckingPosition) == KingIsAt) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return null;
    }
}
