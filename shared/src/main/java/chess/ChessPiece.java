package chess;

import javax.print.attribute.standard.MediaSize;
import java.lang.foreign.ValueLayout;
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



    public class PieceMovesCalculator{

        private ChessBoard board;
        private ChessPosition myPosition;

        public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
            this.board = board;
            this.myPosition = myPosition;
        }

        private boolean IsValidPosition(ChessPosition position) {
            return position.getRow() >= 1 && position.getRow() <= 8 && position.getColumn() >= 1 && position.getColumn() <= 8;
        }

        private String OtherTeam(ChessPosition end) {
            ChessPiece PieceAtEnd = board.getPiece(end);
            if (PieceAtEnd == null) {return "Open";}
            if (PieceAtEnd.getTeamColor() != pieceColor) {return "Capture";}
            return "Same";
        }

        private void ValidateMoveInDiagonal(Collection<ChessMove> moves, ChessPosition start){
            int row = start.getRow();
            int column = start.getColumn();

            while(column < 8 && row < 8) { // up and right
                column++;
                row++;
                ChessPosition end = new ChessPosition(row, column);

                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                } else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                } else {
                    break;
                }
            }

            row = start.getRow();
            column = start.getColumn();

            while(column > 1 && row < 8) { // up and left
                column --;
                row ++;

                ChessPosition end = new ChessPosition(row, column);

                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                }
                else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                }
                else{break;}
            }
            row = start.getRow();
            column = start.getColumn();

            while(column > 1 && row > 1) { // down and left
                column --;
                row --;

                ChessPosition end = new ChessPosition(row, column);

                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                }
                else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                }
                else{break;}
            }
            row = start.getRow();
            column = start.getColumn();

            while(column < 8 && row > 1) { // down and right
                column ++;
                row --;

                ChessPosition end = new ChessPosition(row, column);

                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                }
                else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                }
                else{break;}
            }

        }

        private void ValidateMoveInPlus(Collection<ChessMove> moves, ChessPosition start) {
            int row = start.getRow();
            int column = start.getColumn();

            while(column < 8) { //right
                column++;
                ChessPosition end = new ChessPosition(row, column);

                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                }
                else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                }
                else {
                    break;
                }
            }
            row = start.getRow();
            column = start.getColumn();
            while (column >1){ // left

                column--;
                ChessPosition end = new ChessPosition(row, column);
                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                }
                else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                }
                else {
                    break;
                }
            }
            row = start.getRow();
            column = start.getColumn();
            while (row <8) { //up
                row++;
                ChessPosition end = new ChessPosition(row, column);
                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                }
                else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                }
                else {
                    break;
                }
            }
            row = start.getRow();
            column = start.getColumn();
            while (row > 1) { //down
                row --;
                ChessPosition end = new ChessPosition(row, column);
                if (OtherTeam(end).equals("Open")) {
                    ValidatePositionAndAddMove(moves, start, end);
                }
                else if (OtherTeam(end).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, end);
                    break;
                }
                else {
                    break;
                }
            }
        }

        private void Pawn(Collection<ChessMove> moves, ChessPosition start) {
            int row = start.getRow();
            int column = start.getColumn();



            if (pieceColor == ChessGame.TeamColor.WHITE){ //white pawns
                ChessPosition attackLeft = new ChessPosition(row +1, column-1);
                ChessPosition attackRight = new ChessPosition(row+1, column + 1);
                if (row == 2) {
                    ValidatePositionAndAddMove(moves, start, new ChessPosition(start.getRow() + 1, start.getColumn()));
                    ValidatePositionAndAddMove(moves, start, new ChessPosition(start.getRow() + 2, start.getColumn()));
                }
                else if (OtherTeam(attackRight).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, attackRight);
                }
                else if (OtherTeam(attackLeft).equals("Capture")) {
                    ValidatePositionAndAddMove(moves,start, attackLeft);
                }
                else if (OtherTeam(new ChessPosition(row+1, column)).equals("Capture")) {

                }
                else {
                    ValidatePositionAndAddMove(moves, start, new ChessPosition(row+1, column));
                }
            }
            else{ //black pawns
                ChessPosition attackLeft = new ChessPosition(row -1, column-1);
                ChessPosition attackRight = new ChessPosition(row - 1, column + 1);
                if (row == 7) {
                    ValidatePositionAndAddMove(moves, start, new ChessPosition(start.getRow() - 1, start.getColumn()));
                    ValidatePositionAndAddMove(moves, start, new ChessPosition(start.getRow() - 2, start.getColumn()));
                }
                else if (OtherTeam(attackRight).equals("Capture")) {
                    ValidatePositionAndAddMove(moves, start, attackRight);
                }
                else if (OtherTeam(attackLeft).equals("Capture")) {
                    ValidatePositionAndAddMove(moves,start, attackLeft);
                }
                else if (OtherTeam(new ChessPosition(row-1, column)).equals("Capture")) {

                }
                else {
                    ValidatePositionAndAddMove(moves, start, new ChessPosition(row-1, column));
                }
            }

        }

        private void ValidatePositionAndAddMove(Collection<ChessMove> moves, ChessPosition start, ChessPosition end, PieceType type){
           if (IsValidPosition(end) && OtherTeam(end) == "Capture" || OtherTeam(end) == "Open") {
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
                    Pawn(moves, position);

                    break;
                case ROOK:
                    ValidateMoveInPlus(moves, position);

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
                    ValidateMoveInDiagonal(moves, position);
                    break;
                case QUEEN:
                    ValidateMoveInPlus(moves, position);
                    ValidateMoveInDiagonal(moves, position);
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

