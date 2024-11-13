package ui;

public class PrintBoard {

    private String[][] board;

    public PrintBoard(Boolean isWhitePerspective){
        board = new String[10][10];
        initializeBoard(isWhitePerspective);

    }

    private void initializeBoard(Boolean isWhitePerspective){
        if (isWhitePerspective){
            initializeWhiteBoard();
        }
        else{
            initializeBlackBoard();
        }
    }


    private void initializeWhiteBoard(){

        for (int i = 1; i < 9; i++) {
            board[0][i] = " " + (char)('A' + i - 1) + " ";
            board[9][i] = " " + (char)('A' + i - 1) + " ";
        }

        for (int i = 1; i < 9; i++) {
            board[i][0] = " " + (char)('A' + i - 1) + " ";
            board[i][9] = " " + (char)('A' + i - 1) + " ";
        }

        board[1][1] = EscapeSequences.WHITE_ROOK;
        board[1][2] = EscapeSequences.WHITE_KNIGHT;
        board[1][3] = EscapeSequences.WHITE_BISHOP;
        board[1][4] = EscapeSequences.WHITE_QUEEN;
        board[1][5] = EscapeSequences.WHITE_KING;
        board[1][6] = EscapeSequences.WHITE_BISHOP;
        board[1][7] = EscapeSequences.WHITE_KNIGHT;
        board[1][8] = EscapeSequences.WHITE_ROOK;

        for (int i = 1; i < 9; i++){
            board[2][i] = EscapeSequences.WHITE_PAWN;
        }

        board[8][1] = EscapeSequences.BLACK_ROOK;
        board[8][2] = EscapeSequences.BLACK_KNIGHT;
        board[8][3] = EscapeSequences.BLACK_BISHOP;
        board[8][4] = EscapeSequences.BLACK_QUEEN;
        board[8][5] = EscapeSequences.BLACK_KING;
        board[8][6] = EscapeSequences.BLACK_BISHOP;
        board[8][7] = EscapeSequences.BLACK_KNIGHT;
        board[8][8] = EscapeSequences.BLACK_ROOK;

        for (int i = 1; i < 9; i++){
            board[7][i] = EscapeSequences.BLACK_PAWN;
        }
    }


    private void initializeBlackBoard(){

        for (int i = 1; i <= 8; i++) {
            board[0][i] = " " + (char)('A' + i - 1) + " ";
            board[9][i] = " " + (char)('A' + i - 1) + " ";
        }

        board[1][1] = EscapeSequences.BLACK_ROOK;
        board[1][2] = EscapeSequences.BLACK_KNIGHT;
        board[1][3] = EscapeSequences.BLACK_BISHOP;
        board[1][4] = EscapeSequences.BLACK_QUEEN;
        board[1][5] = EscapeSequences.BLACK_KING;
        board[1][6] = EscapeSequences.BLACK_BISHOP;
        board[1][7] = EscapeSequences.BLACK_KNIGHT;
        board[1][8] = EscapeSequences.BLACK_ROOK;

        for (int i = 1; i <= 8; i++) {
            board[2][i] = EscapeSequences.BLACK_PAWN;
        }

        // White pieces
        board[8][1] = EscapeSequences.WHITE_ROOK;
        board[8][2] = EscapeSequences.WHITE_KNIGHT;
        board[8][3] = EscapeSequences.WHITE_BISHOP;
        board[8][4] = EscapeSequences.WHITE_QUEEN;
        board[8][5] = EscapeSequences.WHITE_KING;
        board[8][6] = EscapeSequences.WHITE_BISHOP;
        board[8][7] = EscapeSequences.WHITE_KNIGHT;
        board[8][8] = EscapeSequences.WHITE_ROOK;


        for (int i = 1; i < 9; i++){
            board[7][i] = EscapeSequences.WHITE_PAWN;
        }
    }

    public void drawBoard() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {

                if (isBorder(row, col)) {
                    System.out.print(EscapeSequences.SET_BG_COLOR_BLACK);
                    System.out.print(board[row][col] == null ? EscapeSequences.EMPTY : board[row][col]);
                } else {

                    if ((row + col) % 2 == 0) {
                        System.out.print(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
                    } else {
                        System.out.print(EscapeSequences.SET_BG_COLOR_DARK_GREY);
                    }
                    System.out.print(board[row][col] == null ? EscapeSequences.EMPTY : board[row][col]);
                }
                System.out.print(EscapeSequences.RESET_BG_COLOR);
            }
            System.out.println();
        }
    }

    private boolean isBorder(int row, int col) {
        return row == 0 || row == 9 || col == 0 || col == 9;
    }
}
