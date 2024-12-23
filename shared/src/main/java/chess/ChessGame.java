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
        setTeamTurn(TeamColor.WHITE);
        currentBoard.resetBoard();
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
        Collection<ChessMove> validMoves = new ArrayList<>();
        if (currentBoard.getPiece(startPosition) != null) {
            ChessPiece currPiece = getBoard().getPiece(startPosition);
            Collection<ChessMove> possibilities = new ArrayList<>(currPiece.pieceMoves(getBoard(), startPosition));
            if (!possibilities.isEmpty()) {
                for (ChessMove move : possibilities) {
                    ChessBoard possibleBoard = new ChessBoard(currentBoard);
                    currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                    currentBoard.addPiece(move.getStartPosition(), null);
                    if (!isInCheck(possibleBoard.getPiece(startPosition).getTeamColor())) {
                        validMoves.add(move);
                    }
                    currentBoard.copyFrom(possibleBoard);
                }
            }
            return validMoves;
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (getBoard().getPiece(move.getStartPosition()) != null) {
            if (getBoard().getPiece(move.getStartPosition()).getTeamColor() == getTeamTurn()) {
                Collection<ChessMove> movesList = validMoves(move.getStartPosition());
                if (!movesList.isEmpty()) {
                    if (movesList.contains(move)) {
                        if (currentBoard.getPiece(move.getStartPosition()).getPieceType() == ChessPiece.PieceType.PAWN && move.getPromotionPiece() != null) {
                            currentBoard.addPiece(move.getEndPosition(), new ChessPiece(currentBoard.getPiece(move.getStartPosition()).getTeamColor(),move.getPromotionPiece()));
                        }
                        else {
                            currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                        }
                        currentBoard.addPiece(move.getStartPosition(), null);
                        if (getTeamTurn() == TeamColor.BLACK) {
                            setTeamTurn(TeamColor.WHITE);
                        }
                        else {
                            setTeamTurn(TeamColor.BLACK);
                        }
                    }
                    else {
                        throw new InvalidMoveException();
                    }
                }
                else {
                    throw new InvalidMoveException();
                }
            }
            else {
                throw new InvalidMoveException();
            }
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
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (currentBoard.getPiece(new ChessPosition(i,j)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING && currentBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                        kingPosition = new ChessPosition(i, j);
                        break;
                    }
                }
            }
            if (kingPosition != null) {
                break;
            }
        }
        if (kingPosition == null) {
            return false;
        }
        for (int i = 0; i < 9; i ++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                ChessPiece currPiece = currentBoard.getPiece(currPosition);
                if ( currPiece != null && currPiece.getTeamColor() != teamColor) {
                    Collection<ChessMove> movesList = currPiece.pieceMoves(currentBoard, currPosition);
                    for (ChessMove move : movesList) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
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
        ChessPosition kingPosition = null;
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                if (currentBoard.getPiece(new ChessPosition(i,j)) != null) {
                    if (currentBoard.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING && currentBoard.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                        kingPosition = new ChessPosition(i, j);
                        break;
                    }
                }
            }
            if (kingPosition != null) {
                break;
            }
        }
        assert kingPosition != null;
        ChessPiece kingPiece = currentBoard.getPiece(kingPosition);
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(getBoard(), kingPosition);
        if (!kingMoves.isEmpty()) {
            for (ChessMove move : kingMoves) {
                ChessBoard possibleBoard = new ChessBoard(currentBoard);
                currentBoard.addPiece(move.getEndPosition(), currentBoard.getPiece(move.getStartPosition()));
                currentBoard.addPiece(move.getStartPosition(), null);
                if (!isInCheck(teamColor)) {
                    currentBoard.copyFrom(possibleBoard);
                    return false;
                }
                currentBoard.copyFrom(possibleBoard);
            }
        }
        for (int i = 1; i < 9; i++) {
            for (int j = 1; j < 9; j++) {
                ChessPosition currPosition = new ChessPosition(i,j);
                ChessPiece currPiece = currentBoard.getPiece(currPosition);
                if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                    Collection<ChessMove> possibleMoves = currPiece.pieceMoves(getBoard(), currPosition);
                    if (!possibleMoves.isEmpty()) {
                        for (ChessMove move : possibleMoves) {
                            ChessBoard possibleBoard = new ChessBoard(currentBoard);
                            currentBoard.addPiece(move.getEndPosition(), possibleBoard.getPiece(move.getStartPosition()));
                            currentBoard.addPiece(move.getStartPosition(), null);
                            if (!isInCheck(teamColor)) {
                                currentBoard.copyFrom(possibleBoard);
                                return false;
                            }
                            currentBoard.copyFrom(possibleBoard);
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            for (int i = 1; i < 9; i++) {
                for (int j = 1; j < 9; j++) {
                    ChessPosition currPosition = new ChessPosition(i,j);
                    ChessPiece currPiece = currentBoard.getPiece(currPosition);
                    if (currPiece != null && currPiece.getTeamColor() == teamColor) {
                        if (!validMoves(currPosition).isEmpty()) {
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */

    public void setBoard(ChessBoard board) {
        currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return currentBoard;
    }


}
