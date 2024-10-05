package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Stack;


/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamColor;
    private Stack<MoveHistory> tempMoveHistory;

    public ChessGame() {
        this.board = new ChessBoard();
        this.tempMoveHistory = new Stack<>();
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
        this.teamColor = team;
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
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        } else {
            Collection<ChessMove> TestMoves = piece.pieceMoves(board, startPosition);
            return TestMoves;
        }
    }

    private boolean IsMovingIntoCheck(ChessMove move, ChessBoard board) {
        ChessPosition end = move.getEndPosition();

        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition CheckingPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(CheckingPosition);
                if (piece != null) {
                    Collection<ChessMove> moves = piece.pieceMoves(board, CheckingPosition);
                    for (ChessMove pieceMove : moves) {
                        if (pieceMove.getEndPosition().equals(end)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor team){
        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition CheckingPosition = new ChessPosition(row, col);
                if (board.getPiece(CheckingPosition) != null){
                ChessPiece piece = board.getPiece(CheckingPosition);
                    if (piece.getPieceType().equals(ChessPiece.PieceType.KING) && piece.getTeamColor().equals(team)) {
                        return CheckingPosition;
                    }
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
        ChessPiece piece = board.getPiece(move.getStartPosition());

        if (piece == null || piece.getTeamColor() != teamColor){
            throw new InvalidMoveException("Invalid Move: There is no piece at that start position");
        }
        Collection<ChessMove> validMoves = piece.pieceMoves(board, move.getStartPosition());

        if (!validMoves.contains(move) && putsInCheck(move.getStartPosition())){
            throw new InvalidMoveException("Invalid Move: That was not a valid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition KingIsAt = findKing(teamColor);
        if (KingIsAt == null){
            return false;
        }

        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition CheckingPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(CheckingPosition);

                if (piece != null){
                    Collection<ChessMove> moves = piece.pieceMoves(board, CheckingPosition);
                    Collection<ChessPosition> endMoves = new ArrayList<>();
                    for (ChessMove move : moves) {
                        endMoves.add(move.getEndPosition());
                    }

                    if (teamColor != piece.getTeamColor() && endMoves.contains(KingIsAt)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean putsInCheck(ChessPosition kingPosition){
        for (int row = 1; row < 9; row++){
            for (int col = 1; col < 9; col++){
                ChessPosition CheckingPosition = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(CheckingPosition);

                if (piece != null){
                    Collection<ChessMove> moves = piece.pieceMoves(board, CheckingPosition);
                    Collection<ChessPosition> endMoves = new ArrayList<>();
                    for (ChessMove move : moves) {
                        endMoves.add(move.getEndPosition());
                    }

                    if (teamColor != piece.getTeamColor() && endMoves.contains(kingPosition)) {
                        return true;
                    }
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
        if (isInCheck(teamColor)){
            ChessPosition kingIsAt = findKing(teamColor);
            if (kingIsAt == null){
                return false;
            }

            ChessPiece king = board.getPiece(kingIsAt);
            Collection<ChessMove> kingMoves = king.pieceMoves(board, kingIsAt);

            for (ChessMove move : kingMoves) {
                makeTempMove(king, move.getStartPosition(), move.getEndPosition());
                if (!isInCheck(teamColor)) {
                    undoTempMove(king, move.getStartPosition(), move.getEndPosition());
                    return false;
                }
                undoTempMove(king, move.getStartPosition(), move.getEndPosition());
            }

            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = board.getPiece(position);

                    if (piece != null && piece.getTeamColor() == teamColor) {
                        Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);
                        for (ChessMove move : pieceMoves) {
                            makeTempMove(piece, move.getStartPosition(), move.getEndPosition());
                            if (!isInCheck(teamColor)) {
                                undoTempMove(piece, move.getStartPosition(), move.getEndPosition());
                                return false;
                            }
                            undoTempMove(piece, move.getStartPosition(), move.getEndPosition());
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void makeTempMove(ChessPiece piece, ChessPosition startPosition, ChessPosition end){
        ChessPiece capturedPiece = board.getPiece(end);

        board.addPiece(end, piece);
        board.addPiece(startPosition, null);

        tempMoveHistory.push(new MoveHistory(startPosition, end, piece, capturedPiece));
    }


    private void undoTempMove(ChessPiece piece, ChessPosition position, ChessPosition end){
        if (!tempMoveHistory.isEmpty()){
            MoveHistory lastMove = tempMoveHistory.pop();

            board.addPiece(lastMove.getStartPosition(), lastMove.getMovedPiece());
            board.addPiece(lastMove.getEndPosition(), lastMove.capturedPiece);
        }
    }




    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves and not being in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        // If the team is in check, it's not a stalemate
        if (isInCheck(teamColor)) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> pieceMoves = piece.pieceMoves(board, position);

                    for (ChessMove move : pieceMoves) {
                        makeTempMove(piece, move.getStartPosition(), move.getEndPosition());
                        boolean inCheck = isInCheck(teamColor);
                        undoTempMove(piece, move.getStartPosition(), move.getEndPosition());

                        if (!inCheck) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard(){
        return board;
    }


    public class MoveHistory {
        private ChessPosition startPosition;
        private ChessPosition endPosition;
        private ChessPiece movedPiece;
        private ChessPiece capturedPiece;

        public MoveHistory(ChessPosition startPosition, ChessPosition endPosition, ChessPiece movedPiece, ChessPiece capturedPiece) {
            this.startPosition = startPosition;
            this.endPosition = endPosition;
            this.movedPiece = movedPiece;
            this.capturedPiece = capturedPiece;
        }

        public ChessPosition getStartPosition() {
            return startPosition;
        }

        public ChessPosition getEndPosition() {
            return endPosition;
        }

        public ChessPiece getMovedPiece() {
            return movedPiece;
        }

        public ChessPiece getCapturedPiece() {
            return capturedPiece;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(getBoard(), chessGame.getBoard()) && teamColor == chessGame.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBoard(), teamColor);
    }

}
