import com.sun.nio.sctp.NotificationHandler;
import exception.ResponseException;
import model.UserData;

import java.util.Arrays;

public class ChessClient {
    private final ServerFacade server;
    private final String serverUrl;

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
            server.createUser(user);
            return String.format("%s is now registered", user.username());
        }
        throw new ResponseException(400, "Expected: <yourname> <yourpassword> <youremail>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 3) {
            UserData user = new UserData(params[0], params[1], params[2]);
            server.createUser(user);
            return String.format("%s is now registered", user.username());
        }
        throw new ResponseException(400, "Expected: <yourname> <yourpassword>");
    }

    public String createGame(String... params) throws ResponseException {
        if (params.length >= 1) {
            int gameID = server.createGame(params[0]);
            return String.format("Created game: %s - %d", params[0], gameID);
        }
        throw new ResponseException(400, "Expected: <gameName>");
    }

    public String joinGame(String... params) throws ResponseException {
        if (params.length >= 2) {
            int gameID = Integer.parseInt(params[0]);
            server.joinGame(gameID, params[1]);
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
}
