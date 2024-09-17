package chess;

import java.util.Arrays;

import static chess.ChessGame.TeamColor.BLACK;
import static chess.ChessGame.TeamColor.WHITE;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
        InitializeBoard();
    }
    public void InitializeBoard(){
        
        for (int i = 0; i < 8; i++){
            //white on bottom then black on top
            board[1][i] = new ChessPiece(WHITE, ChessPiece.PieceType.PAWN);
            board[6][i] = new ChessPiece(BLACK, ChessPiece.PieceType.PAWN);
            if(i == 0 || i == 7) {
                board[0][i] = new ChessPiece(WHITE, ChessPiece.PieceType.ROOK);
                board[7][i] = new ChessPiece(BLACK, ChessPiece.PieceType.ROOK);
            }
            if(i == 1 || i == 6) {
                board[0][i] = new ChessPiece(WHITE, ChessPiece.PieceType.KNIGHT);
                board[7][i] = new ChessPiece(BLACK, ChessPiece.PieceType.KNIGHT);
            }
            if(i == 2 || i == 5) {
                board[0][i] = new ChessPiece(WHITE, ChessPiece.PieceType.BISHOP);
                board[7][i] = new ChessPiece(BLACK, ChessPiece.PieceType.BISHOP);
            }
            if(i == 3) {
                board[0][i] = new ChessPiece(WHITE, ChessPiece.PieceType.QUEEN);
                board[7][i] = new ChessPiece(BLACK, ChessPiece.PieceType.KING);
            }
            if(i == 4) {
                board[0][i] = new ChessPiece(WHITE, ChessPiece.PieceType.KING);
                board[7][i] = new ChessPiece(BLACK, ChessPiece.PieceType.QUEEN);
            }
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        //add piece to board @ position
        board[(position.getRow()-1)][(position.getColumn()-1)] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        //return piece so that team color and piece type can be used for other functions such as moves
        if ((0 < position.getRow()) && (position.getRow() < 9) && (0 < position.getColumn()) && (position.getColumn() < 9)) {
            if (board[(position.getRow()-1)][(position.getColumn()-1)] != null) {
                return board[(position.getRow()-1)][(position.getColumn()-1)];
            }
            else {
                return null;
            }
        }
        else{
            return null;
        }
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //reset board to completely blank
        //edit later to put pieces back in default position using set board function
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                board[i][j] = null;
            }
        }
        InitializeBoard();
        //board = new ChessBoard();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessBoard that = (ChessBoard) o;
        return Arrays.equals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(board);
    }
}
