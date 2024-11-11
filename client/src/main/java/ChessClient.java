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
