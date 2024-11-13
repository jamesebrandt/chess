package ui;

public class DisplayBoardBlack {

    private String[][] board;

    public DisplayBoardBlack(){
        board = new String[8][8];
        initializeBoard();
    }

    private void initializeBoard(){

        board[7][0] = EscapeSequences.WHITE_ROOK;
        board[7][1] = EscapeSequences.WHITE_KNIGHT;
        board[7][2] = EscapeSequences.WHITE_BISHOP;
        board[7][3] = EscapeSequences.WHITE_QUEEN;
        board[7][4] = EscapeSequences.WHITE_KING;
        board[7][5] = EscapeSequences.WHITE_BISHOP;
        board[7][6] = EscapeSequences.WHITE_KNIGHT;
        board[7][7] = EscapeSequences.WHITE_ROOK;

        for (int i = 0; i < 8; i++){
            board[6][i] = EscapeSequences.WHITE_PAWN;
        }

        board[0][0] = EscapeSequences.BLACK_ROOK;
        board[0][1] = EscapeSequences.BLACK_KNIGHT;
        board[0][2] = EscapeSequences.BLACK_BISHOP;
        board[0][3] = EscapeSequences.BLACK_QUEEN;
        board[0][4] = EscapeSequences.BLACK_KING;
        board[0][5] = EscapeSequences.BLACK_BISHOP;
        board[0][6] = EscapeSequences.BLACK_KNIGHT;
        board[0][7] = EscapeSequences.BLACK_ROOK;

        for (int i = 0; i < 8; i++){
            board[1][i] = EscapeSequences.BLACK_PAWN;
        }
    }

    public void printBoard(){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                } else {
                    System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                }

                if (board[row][col] == null) {
                    System.out.print(EscapeSequences.EMPTY);
                } else {
                    System.out.print(board[row][col]);
                }

                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }

    }


}
