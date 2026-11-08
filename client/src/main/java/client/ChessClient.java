package client;

import chess.ChessGame;
import exception.ResponseException;
import model.GameData;
import ui.BoardPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessClient {
    private final ServerFacade server;
    private String auth = null;
    private String username = null;
    // Games from the last "list", so users can refer to them by number instead of ID
    private List<GameData> lastListedGames = new ArrayList<>();

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public boolean isLoggedIn() {
        return auth != null;
    }

    public String eval(String input) {
        try {
            var tokens = input.trim().split("\\s+");
            var cmd = tokens[0].toLowerCase();
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (!isLoggedIn()) {
                return switch (cmd) {
                    case "register" -> register(params);
                    case "login" -> login(params);
                    case "quit" -> "quit";
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
            return ex.getMessage();
        }
    }

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
        server.joinGame(auth, game.gameID(), color.name());
        return String.format("Joined '%s' as %s.\n%s", game.gameName(), color, drawBoard(game, color));
    }

    public String observeGame(String... params) throws ResponseException {
        if (params.length != 1) {
            throw new ResponseException(400, "Expected: observe <NUMBER>");
        }
        var game = gameFromNumber(params[0]);
        return String.format("Observing '%s'.\n%s", game.gameName(), drawBoard(game, ChessGame.TeamColor.WHITE));
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
        return lastListedGames.get(index);
    }

    private static String drawBoard(GameData game, ChessGame.TeamColor perspective) {
        var chessGame = game.game() != null ? game.game() : new ChessGame();
        return BoardPrinter.draw(chessGame.getBoard(), perspective);
    }

    private static String playerName(String name) {
        return name == null ? "(open)" : name;
    }
}
