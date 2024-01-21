package chess;

import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team;
    private ChessBoard board;
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
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
        throw new RuntimeException("Not implemented");
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
        throw new RuntimeException("Not implemented");
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
        this.board = board;
        //add white pieces first

        for (int i = 0; i < 8; i++){
            ChessPosition position = new ChessPosition(1,i);
            ChessPiece piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.PAWN);
            board.addPiece(position, piece);
        }
        ChessPosition rook1position = new ChessPosition(0,0);
        ChessPiece rook1piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board.addPiece(rook1position, rook1piece);
        ChessPosition rook2position = new ChessPosition(0,7);
        ChessPiece rook2piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        board.addPiece(rook2position, rook2piece);

        ChessPosition knight1position = new ChessPosition(0,1);
        ChessPiece knight1piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board.addPiece(knight1position, knight1piece);
        ChessPosition knight2position = new ChessPosition(0,6);
        ChessPiece knight2piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        board.addPiece(knight2position, knight2piece);

        ChessPosition bishop1position = new ChessPosition(0,2);
        ChessPiece bishop1piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board.addPiece(bishop1position, bishop1piece);
        ChessPosition bishop2position = new ChessPosition(0,5);
        ChessPiece bishop2piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        board.addPiece(bishop2position, bishop2piece);

        ChessPosition queen1position = new ChessPosition(0,3);
        ChessPiece queen1piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        board.addPiece(queen1position, queen1piece);
        ChessPosition king1position = new ChessPosition(0,4);
        ChessPiece king1piece = new ChessPiece(TeamColor.WHITE, ChessPiece.PieceType.KING);
        board.addPiece(king1position, king1piece);

        //add black pieces now

        for (int i = 0; i < 8; i++){
            ChessPosition position = new ChessPosition(6,i);
            ChessPiece piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.PAWN);
            board.addPiece(position, piece);
        }

        ChessPosition rook3position = new ChessPosition(7,0);
        ChessPiece rook3piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board.addPiece(rook3position, rook3piece);
        ChessPosition rook4position = new ChessPosition(7,7);
        ChessPiece rook4piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        board.addPiece(rook4position, rook4piece);

        ChessPosition knight3position = new ChessPosition(7,1);
        ChessPiece knight3piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board.addPiece(knight3position, knight3piece);
        ChessPosition knight4position = new ChessPosition(7,6);
        ChessPiece knight4piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        board.addPiece(knight4position, knight4piece);

        ChessPosition bishop3position = new ChessPosition(7,2);
        ChessPiece bishop3piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board.addPiece(bishop3position, bishop3piece);
        ChessPosition bishop4position = new ChessPosition(7,5);
        ChessPiece bishop4piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        board.addPiece(bishop4position, bishop4piece);

        ChessPosition queen2position = new ChessPosition(7,4);
        ChessPiece queen2piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        board.addPiece(queen2position, queen2piece);
        ChessPosition king2position = new ChessPosition(7,3);
        ChessPiece king2piece = new ChessPiece(TeamColor.BLACK, ChessPiece.PieceType.KING);
        board.addPiece(king2position, king2piece);

    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
