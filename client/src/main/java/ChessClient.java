import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.Arrays;
import java.util.Objects;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    private String auth;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "creategame" -> createGame(params);
                case "joingame" -> joinGame(params);
                case "listgames" -> listGames();
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            UserData user = new UserData(params[0], params[1], params[2]);
            AuthData auth = server.createUser(user);
            this.auth = auth.authToken();
            return String.format("%s is now registered", user.username());
        }
        throw new ResponseException(400, "Expected: <yourname> <yourpassword> <youremail>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 3) {
            UserData user = new UserData(params[0], params[1], params[2]);
            AuthData auth = server.createUser(user);
            this.auth = auth.authToken();
            return String.format("%s is now registered", user.username());
        }
        throw new ResponseException(400, "Expected: <yourname> <yourpassword>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            int gameID = server.createGame(params[0]);
            printBoard(gameID);
            return String.format("Created game: %s - %d", params[0], gameID);
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            int gameID = Integer.parseInt(params[0]);
            server.joinGame(gameID, params[1]);
            if (Objects.equals(params[1], "white") || Objects.equals(params[1], "observer")) {
                printWhiteBoard(gameID);
            }
            else {
                printBlackBoard(gameID);
            }
            return String.format("You joined %s team in game %d", params[1], gameID);
        }
        throw new ResponseException(400, "Expected: <gameID> <teamColor>");
    }

    public String listGames() throws ResponseException {
        String allGames = server.listGames();
        if (allGames == null) {
            return "There are no games currently";
        }
        return "Games: \n" + allGames;
    }

    public String logout() throws ResponseException {
        server.logout();
        return "Successfully logged out";
    }

    public String help() {
        return """
                - register <yourname> <yourpassword> <youremail>
                - login <yourname> <yourpassword>
                - createGame <gameName>
                - joinGame <gameID>
                - listGames
                - logout
                - quit
                """;
    }

    public void printBoard(int gameID) throws ResponseException {
        printWhiteBoard(gameID);
        printBlackBoard(gameID);
    }

    public void printWhiteBoard(int gameID) throws ResponseException {
        //top part
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY + " A   B  C  D   E   F  G   H " + EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
        String tileColor;
        String white = SET_TEXT_COLOR_GREEN;
        String black = SET_TEXT_COLOR_RED;
        ChessBoard game = server.getGame(gameID).getBoard();
        StringBuilder row;

        for (int i = 8; i > 0; i--) {
            row = new StringBuilder(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + ' ' + i + ' ');
            if (i % 2 == 0) {
                tileColor = SET_BG_COLOR_WHITE;
            }
            else {
                tileColor = SET_BG_COLOR_BLACK;
            }
            for (int j = 1; j < 9; j++) {
                ChessPosition tile = new ChessPosition(i, j);
                ChessPiece pieces = game.getPiece(tile);
                row.append(tileColor);
                if (tileColor.equals(SET_BG_COLOR_BLACK)) {
                    tileColor = SET_BG_COLOR_WHITE;
                } else {
                    tileColor = SET_BG_COLOR_BLACK;
                }
                if (pieces == null) {
                    row.append(EMPTY);
                }
                else if (pieces.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    switch (pieces.getPieceType()) {
                        case KING -> row.append(white).append(WHITE_KING);
                        case QUEEN -> row.append(white).append(WHITE_QUEEN);
                        case BISHOP -> row.append(white).append(WHITE_BISHOP);
                        case ROOK -> row.append(white).append(WHITE_ROOK);
                        case KNIGHT -> row.append(white).append(WHITE_KNIGHT);
                        case PAWN -> row.append(white).append(WHITE_PAWN);
                    }
                }
                else {
                    switch (pieces.getPieceType()) {
                        case KING -> row.append(black).append(BLACK_KING);
                        case QUEEN -> row.append(black).append(BLACK_QUEEN);
                        case BISHOP -> row.append(black).append(BLACK_BISHOP);
                        case ROOK -> row.append(black).append(BLACK_ROOK);
                        case KNIGHT -> row.append(black).append(BLACK_KNIGHT);
                        case PAWN -> row.append(black).append(BLACK_PAWN);
                    }
                }
            }
            row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + ' ').append(i).append(' ').append(RESET_BG_COLOR);
            System.out.println(row);
        }

        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY + " A   B  C  D   E   F  G   H " + EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    public void printBlackBoard(int gameID) throws ResponseException {
        //top part
        System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY + " A   B  C  D   E   F  G   H " + EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
        String tileColor;
        String white = SET_TEXT_COLOR_GREEN;
        String black = SET_TEXT_COLOR_RED;
        ChessBoard game = server.getGame(gameID).getBoard();
        StringBuilder row;

        for (int i = 1; i < 9; i++) {
                row = new StringBuilder(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + ' ' + i + ' ');
                if (i % 2 == 0) {
                    tileColor = SET_BG_COLOR_WHITE;
                }
                else {
                    tileColor = SET_BG_COLOR_BLACK;
                }
                for (int j = 1; j < 9; j++) {
                    ChessPosition tile = new ChessPosition(i, j);
                    ChessPiece piece = game.getPiece(tile);
                    row.append(tileColor);
                    if (tileColor.equals(SET_BG_COLOR_BLACK)) {
                        tileColor = SET_BG_COLOR_WHITE;
                    } else {
                        tileColor = SET_BG_COLOR_BLACK;
                    }
                    if (piece == null) {
                        row.append(EMPTY);
                    }
                    else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        switch (piece.getPieceType()) {
                            case KING -> row.append(white).append(WHITE_KING);
                            case QUEEN -> row.append(white).append(WHITE_QUEEN);
                            case BISHOP -> row.append(white).append(WHITE_BISHOP);
                            case ROOK -> row.append(white).append(WHITE_ROOK);
                            case KNIGHT -> row.append(white).append(WHITE_KNIGHT);
                            case PAWN -> row.append(white).append(WHITE_PAWN);
                        }
                    }
                    else {
                        switch (piece.getPieceType()) {
                            case KING -> row.append(black).append(BLACK_KING);
                            case QUEEN -> row.append(black).append(BLACK_QUEEN);
                            case BISHOP -> row.append(black).append(BLACK_BISHOP);
                            case ROOK -> row.append(black).append(BLACK_ROOK);
                            case KNIGHT -> row.append(black).append(BLACK_KNIGHT);
                            case PAWN -> row.append(black).append(BLACK_PAWN);
                        }
                    }
                }
                row.append(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + ' ').append(i).append(' ').append(RESET_BG_COLOR);
                System.out.println(row);
            }
            System.out.println(SET_BG_COLOR_BLACK + SET_TEXT_COLOR_WHITE + EMPTY + " A   B  C  D   E   F  G   H " + EMPTY + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }
}
