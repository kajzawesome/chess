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

    TeamColor teamColor;
    ChessBoard currentBoard = new ChessBoard();

    public ChessGame() {
        setBoard(currentBoard);
        setTeamTurn(TeamColor.WHITE);
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
        teamColor = team;
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
        if (isInCheck(currentBoard.getPiece(startPosition).getTeamColor())) {
            if (isInCheckmate(currentBoard.getPiece(startPosition).getTeamColor())) {
                return null;
            }
            //defend -> move king or find piece to move
            return null;
        }
        else {
            ChessPiece currPiece = getBoard().getPiece(startPosition);
            Collection<ChessMove> possibilities = currPiece.pieceMoves(getBoard(), startPosition);
            ChessBoard possibleBoard = currentBoard;
            for (ChessMove move : possibilities) {
                currentBoard = possibleBoard;
                currentBoard.addPiece(move.getStartPosition(), null);
                currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                if (isInCheck(currentBoard.getPiece(startPosition).getTeamColor())) {
                    if (isInCheckmate(currentBoard.getPiece(startPosition).getTeamColor())) {
                        possibilities.remove(move);
                    }
                    possibilities.remove(move);
                }
            }
            return possibilities;
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> movesList = validMoves(move.getStartPosition());
        if (movesList.contains(move)) {
            currentBoard.addPiece(move.getStartPosition(),null);
            currentBoard.addPiece(move.getEndPosition(),currentBoard.getPiece(move.getStartPosition()));
        }
        else {
            throw new InvalidMoveException();
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        //check every piece on other team if they can hit the king then if so see if king or something can move to stop check otherwise check mate
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
        return currentBoard; //fix this for real
    }


}
