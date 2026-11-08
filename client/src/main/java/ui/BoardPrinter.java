package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

/** Draws a chess board in the terminal from white's or black's point of view. */
public class BoardPrinter {

    private static final String BORDER = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE;
    private static final String LIGHT_SQUARE = SET_BG_COLOR_WHITE;
    private static final String DARK_SQUARE = SET_BG_COLOR_BLACK;
    private static final String WHITE_PIECE = SET_TEXT_COLOR_GREEN;
    private static final String BLACK_PIECE = SET_TEXT_COLOR_RED;

    public static String draw(ChessBoard board, ChessGame.TeamColor perspective) {
        boolean whiteView = perspective != ChessGame.TeamColor.BLACK;
        StringBuilder out = new StringBuilder();

        out.append(header(whiteView));
        for (int i = 0; i < 8; i++) {
            int row = whiteView ? 8 - i : i + 1;
            out.append(BORDER).append(' ').append(row).append(' ');
            for (int j = 0; j < 8; j++) {
                int col = whiteView ? j + 1 : 8 - j;
                // a1 is dark, h1 is light: a square is light when row + col is odd
                out.append((row + col) % 2 == 1 ? LIGHT_SQUARE : DARK_SQUARE);
                out.append(pieceString(board.getPiece(new ChessPosition(row, col))));
            }
            out.append(BORDER).append(' ').append(row).append(' ')
                    .append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append('\n');
        }
        out.append(header(whiteView));
        return out.toString();
    }

    private static String header(boolean whiteView) {
        StringBuilder out = new StringBuilder(BORDER).append("   ");
        for (int j = 0; j < 8; j++) {
            char letter = (char) (whiteView ? 'a' + j : 'h' - j);
            //   (em space) roughly matches the width of a piece glyph
            out.append(' ').append(letter).append(' ');
        }
        return out.append("   ").append(RESET_BG_COLOR).append(RESET_TEXT_COLOR).append('\n').toString();
    }

    private static String pieceString(ChessPiece piece) {
        if (piece == null) {
            return EMPTY;
        }
        boolean white = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        String symbol = switch (piece.getPieceType()) {
            case KING -> white ? WHITE_KING : BLACK_KING;
            case QUEEN -> white ? WHITE_QUEEN : BLACK_QUEEN;
            case BISHOP -> white ? WHITE_BISHOP : BLACK_BISHOP;
            case KNIGHT -> white ? WHITE_KNIGHT : BLACK_KNIGHT;
            case ROOK -> white ? WHITE_ROOK : BLACK_ROOK;
            case PAWN -> white ? WHITE_PAWN : BLACK_PAWN;
        };
        return (white ? WHITE_PIECE : BLACK_PIECE) + symbol;
    }
}
