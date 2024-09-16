package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.pieceColor = pieceColor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceType == that.pieceType && pieceColor == that.pieceColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceType, pieceColor);
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

    private PieceType pieceType;
    private ChessGame.TeamColor pieceColor;

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
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

        private boolean IsValidPosition(ChessPosition position) {
            return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
        }

        private void ValidatePositionAndAddMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, PieceType type){
           if (IsValidPosition(end)) {

               moves.add(new ChessMove(start, end, type));
           }
        }

        private void ValidatePositionAndAddMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end){// sets null as default value
            ValidatePositionAndAddMove(moves, start, end, null);
        }


        public Collection<ChessMove> calculateMoves(ChessPiece piece, ChessPosition position) {
            Collection<ChessMove> moves = new ArrayList<>();


            switch (piece.getPieceType()) {
                case PAWN:
                    // Add pawn move logic here
                    break;
                case ROOK:
                    //while (IsValidPosition(new ChessPosition(position.getRow()+8) )

                    break;
                case KNIGHT:
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()+2, position.getColumn()+1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()+2, position.getColumn()-1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()+1, position.getColumn()+2));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()-1, position.getColumn()+2));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()-2, position.getColumn()-1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()-2, position.getColumn()+1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()-1, position.getColumn()-2));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()+1, position.getColumn()-2));
                    break;
                case BISHOP:
                    // Add bishop move logic here
                    break;
                case QUEEN:
                    // Add queen move logic here
                    break;
                case KING:
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()+1, position.getColumn()+1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()-1, position.getColumn()));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()+1, position.getColumn()));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow(), position.getColumn()+1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()+1, position.getColumn()-1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow(), position.getColumn()-1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()-1, position.getColumn()-1));
                    ValidatePositionAndAddMove(moves, position, new ChessPosition(position.getRow()-1, position.getColumn()+1));

                    break;
            }

            return moves;
        }
    }
}

