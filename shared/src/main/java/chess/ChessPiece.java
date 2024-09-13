package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        switch(getPieceType()) {
            case PAWN:
                // Add pawn move logic here
                break;
            case ROOK:
                // Add rook move logic here
                break;
            case KNIGHT:
                // Add knight move logic here
                break;
            case BISHOP:
                // Add bishop move logic here
                break;
            case QUEEN:
                // Add queen move logic here
                break;
            case KING:
                // Add king move logic here
                break;
        }
        throw new RuntimeException("Not implemented");
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        PieceMovesCalculator calculator = new PieceMovesCalculator(board, myPosition);
        ChessPiece piece = board.getPiece(myPosition);

        if (piece == null) {return Collections.emptyList();}

        return calculator.calculateMoves(piece, myPosition);
    }
    public class PieceMovesCalculator {

        private ChessBoard board;
        private ChessPosition myPosition;

        public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
            this.board = board;
            this.myPosition = myPosition;
        }

        public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessPosition position) {
            Collection<ChessMove> moves = new ArrayList<>();
//
//            // Logic to determine moves based on piece type
//            switch () {
//                case PAWN:
//                    // Add pawn move logic here
//                    break;
//                case ROOK:
//                    // Add rook move logic here
//                    break;
//                case KNIGHT:
//                    // Add knight move logic here
//                    break;
//                case BISHOP:
//                    // Add bishop move logic here
//                    break;
//                case QUEEN:
//                    // Add queen move logic here
//                    break;
//                case KING:
//                    // Add king move logic here
//                    break;
//            }
//
            return moves;
        }
    }
}

