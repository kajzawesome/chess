package client;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import model.GameData;
import ui.BoardPrinter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import static ui.EscapeSequences.*;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;
    // Where to print messages that arrive from the server while the user is typing
    private final Consumer<String> display;

    private String auth = null;
    private String username = null;
    // Games from the last "list", so users can refer to them by number instead of ID
    private List<GameData> lastListedGames = new ArrayList<>();

    // In-game state; set while playing or observing
    private WebSocketFacade ws = null;
    private Integer gameID = null;
    private ChessGame.TeamColor playerColor = null; // null when observing
    private volatile ChessGame currentGame = null;
    private boolean confirmingResign = false;

    public ChessClient(String serverUrl, Consumer<String> display) {
        this.server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.display = display;
    }

    public boolean isLoggedIn() {
        return auth != null;
    }

    public boolean isInGame() {
        return gameID != null;
    }

    public String eval(String input) {
        try {
            var tokens = input.trim().split("\\s+");
            var cmd = tokens[0].toLowerCase();
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (confirmingResign) {
                return confirmResign(cmd);
            }
            if (!isLoggedIn()) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
                    default -> help();
                };
            }
            if (isInGame()) {
                return switch (cmd) {
                    case "redraw" -> redraw();
                    case "move" -> makeMove(params);
                    case "highlight" -> highlight(params);
                    case "resign" -> resign();
                    case "leave" -> leave();
                    default -> help();
                };
            }
            return switch (cmd) {
                case "create" -> createGame(params);
                case "list" -> listGames();
                case "play", "join" -> playGame(params);
                case "observe" -> observeGame(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return SET_TEXT_COLOR_RED + ex.getMessage();
        }
    }

    // ---------- logged out ----------

    public String register(String... params) throws ResponseException {
        if (params.length != 3) {
            throw new ResponseException(400, "Expected: register <USERNAME> <PASSWORD> <EMAIL>");
        }
        var authData = server.register(params[0], params[1], params[2]);
        auth = authData.authToken();
        username = authData.username();
        return String.format("Registered and logged in as %s.\n%s", username, help());
    }

    public String login(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: login <USERNAME> <PASSWORD>");
        }
        var authData = server.login(params[0], params[1]);
        auth = authData.authToken();
        username = authData.username();
        return String.format("Logged in as %s.\n%s", username, help());
    }

    // ---------- logged in ----------

    public String logout() throws ResponseException {
        server.logout(auth);
        auth = null;
        username = null;
        lastListedGames = new ArrayList<>();
        return "Logged out.\n" + help();
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length < 1) {
            throw new ResponseException(400, "Expected: create <NAME>");
        }
        String name = String.join(" ", params);
        server.createGame(auth, name);
        return String.format("Created game '%s'. Type 'list' to see its number.", name);
    }

    public String listGames() throws ResponseException {
        lastListedGames = new ArrayList<>(server.listGames(auth));
        if (lastListedGames.isEmpty()) {
            return "There are no games yet. Create one with: create <NAME>";
        }
        var out = new StringBuilder("Games:\n");
        for (int i = 0; i < lastListedGames.size(); i++) {
            var g = lastListedGames.get(i);
            out.append(String.format("  %d. %-20s white: %-12s black: %s\n", i + 1, g.gameName(),
                    playerName(g.whiteUsername()), playerName(g.blackUsername())));
        }
        return out.toString();
    }

    public String playGame(String... params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "Expected: play <NUMBER> <WHITE|BLACK>");
        }
        var game = gameFromNumber(params[0]);
        ChessGame.TeamColor color;
        try {
            color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseException(400, "Color must be WHITE or BLACK");
        }
        // Rejoining a seat you already hold is fine (e.g. after quitting mid-game)
        String seat = (color == ChessGame.TeamColor.WHITE) ? game.whiteUsername() : game.blackUsername();
        if (!username.equals(seat)) {
            server.joinGame(auth, game.gameID(), color.name());
        }
        enterGame(game.gameID(), color);
        return String.format("Joined '%s' as %s. Type 'help' for game commands.", game.gameName(), color);
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: observe <NUMBER>");
        }
        var game = gameFromNumber(params[0]);
        enterGame(game.gameID(), null);
        return String.format("Observing '%s'. Type 'help' for game commands.", game.gameName());
    }

    private void enterGame(int id, ChessGame.TeamColor color) throws ResponseException {
        ws = new WebSocketFacade(serverUrl, this::onServerMessage);
        gameID = id;
        playerColor = color;
        currentGame = null;
        ws.connect(auth, id);
    }

    // ---------- in a game ----------

    private void onServerMessage(ServerMessage message) {
        switch (message) {
            case LoadGameMessage load -> {
                currentGame = load.getGame();
                display.accept(drawCurrentBoard(null) + turnLine());
            }
            case NotificationMessage note -> display.accept(SET_TEXT_COLOR_YELLOW + note.getMessage());
            case ErrorMessage error -> display.accept(SET_TEXT_COLOR_RED + error.getErrorMessage());
            default -> { }
        }
    }

    public String redraw() throws ResponseException {
        requireGameLoaded();
        return drawCurrentBoard(null) + turnLine();
    }

    public String makeMove(String... params) throws ResponseException {
        if (playerColor == null) {
            throw new ResponseException(400, "Observers can't make moves.");
        }
        if (params.length < 2 || params.length > 3) {
            throw new ResponseException(400, "Expected: move <FROM> <TO> [PROMOTION], e.g. 'move e2 e4' or 'move e7 e8 q'");
        }
        requireGameLoaded();
        var from = parseSquare(params[0]);
        var to = parseSquare(params[1]);
        ChessPiece.PieceType promotion = null;
        if (params.length == 3) {
            promotion = parsePromotion(params[2]);
        } else {
            var piece = currentGame.getBoard().getPiece(from);
            boolean lastRank = to.getRow() == 1 || to.getRow() == 8;
            if (piece != null && piece.getPieceType() == ChessPiece.PieceType.PAWN && lastRank) {
                throw new ResponseException(400, "Pawn promotes: add a piece, e.g. 'move "
                        + params[0] + " " + params[1] + " q' (q, r, b, or n)");
            }
        }
        ws.makeMove(auth, gameID, new ChessMove(from, to, promotion));
        return "";
    }

    public String highlight(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: highlight <SQUARE>, e.g. 'highlight e2'");
        }
        requireGameLoaded();
        var square = parseSquare(params[0]);
        if (currentGame.getBoard().getPiece(square) == null) {
            throw new ResponseException(400, "There's no piece on " + params[0].toLowerCase());
        }
        var targets = currentGame.validMoves(square).stream().map(ChessMove::getEndPosition).toList();
        String board = BoardPrinter.draw(currentGame.getBoard(), perspective(), square, targets);
        return targets.isEmpty() ? board + "That piece has no legal moves." : board;
    }

    public String resign() throws ResponseException {
        if (playerColor == null) {
            throw new ResponseException(400, "Observers can't resign. Type 'leave' to stop watching.");
        }
        confirmingResign = true;
        return "Are you sure you want to resign? (yes/no)";
    }

    private String confirmResign(String answer) throws ResponseException {
        confirmingResign = false;
        if (answer.equals("yes") || answer.equals("y")) {
            ws.resign(auth, gameID);
            return "";
        }
        return "Okay, you didn't resign.";
    }

    public String leave() throws ResponseException {
        ws.leave(auth, gameID);
        ws = null;
        gameID = null;
        playerColor = null;
        currentGame = null;
        return "You left the game.\n" + help();
    }

    public String help() {
        if (!isLoggedIn()) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - create an account
                    login <USERNAME> <PASSWORD> - log in to play
                    quit - exit the program
                    help - show these commands
                    """;
        }
        if (isInGame()) {
            String moves = (playerColor == null) ? "" : """
                    move <FROM> <TO> [PROMOTION] - make a move, e.g. 'move e2 e4' or 'move e7 e8 q'
                    resign - give up the game
                    """;
            return """
                    redraw - draw the board again
                    highlight <SQUARE> - show legal moves for a piece, e.g. 'highlight e2'
                    """ + moves + """
                    leave - leave the game
                    help - show these commands
                    """;
        }
        return """
                create <NAME> - create a game
                list - list games
                play <NUMBER> <WHITE|BLACK> - join a game
                observe <NUMBER> - watch a game
                logout - log out
                quit - exit the program
                help - show these commands
                """;
    }

    // ---------- helpers ----------

    private ChessGame.TeamColor perspective() {
        return playerColor == null ? ChessGame.TeamColor.WHITE : playerColor;
    }

    private String drawCurrentBoard(ChessPosition selected) {
        return BoardPrinter.draw(currentGame.getBoard(), perspective(), selected, List.of());
    }

    private String turnLine() {
        if (currentGame.isGameOver()) {
            return "The game is over.";
        }
        var turn = currentGame.getTeamTurn();
        if (turn == playerColor) {
            return "Your move (" + turn + ").";
        }
        return turn + " to move.";
    }

    private void requireGameLoaded() throws ResponseException {
        if (currentGame == null) {
            throw new ResponseException(400, "Still loading the game, try again in a moment.");
        }
    }

    private static ChessPosition parseSquare(String text) throws ResponseException {
        var s = text.toLowerCase();
        if (s.length() != 2 || s.charAt(0) < 'a' || s.charAt(0) > 'h' || s.charAt(1) < '1' || s.charAt(1) > '8') {
            throw new ResponseException(400, "'" + text + "' isn't a square. Use a letter a-h and a number 1-8, e.g. e2");
        }
        return new ChessPosition(s.charAt(1) - '0', s.charAt(0) - 'a' + 1);
    }

    private static ChessPiece.PieceType parsePromotion(String text) throws ResponseException {
        return switch (text.toLowerCase()) {
            case "q", "queen" -> ChessPiece.PieceType.QUEEN;
            case "r", "rook" -> ChessPiece.PieceType.ROOK;
            case "b", "bishop" -> ChessPiece.PieceType.BISHOP;
            case "n", "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new ResponseException(400, "Promotion piece must be q, r, b, or n");
        };
    }

    private GameData gameFromNumber(String number) throws ResponseException {
        int index;
        try {
            index = Integer.parseInt(number) - 1;
        } catch (NumberFormatException ex) {
            throw new ResponseException(400, "Game number must be a number, e.g. 'play 1 white'");
        }
        if (lastListedGames.isEmpty()) {
            lastListedGames = new ArrayList<>(server.listGames(auth));
        }
        if (index < 0 || index >= lastListedGames.size()) {
            throw new ResponseException(400, "No game with that number. Type 'list' to see games.");
        }
        // Keep the numbers the user saw, but look up current seat info by ID
        int id = lastListedGames.get(index).gameID();
        return server.listGames(auth).stream().filter(g -> g.gameID() == id).findFirst()
                .orElseThrow(() -> new ResponseException(400, "That game no longer exists. Type 'list' to refresh."));
    }

    private static String playerName(String name) {
        return name == null ? "(open)" : name;
    }
}
