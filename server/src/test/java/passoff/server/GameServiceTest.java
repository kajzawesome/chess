package passoff.server;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GameServiceTest {
    static final GameService serviceGames = new GameService();
    static final UserService serviceUsers = new UserService();

    @BeforeEach
    void clear() throws ResponseException {
        serviceGames.deleteGames();
    }

    @Test
    void createGame() throws ResponseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth = serviceUsers.registerUser(user);
        serviceGames.createGame(auth.authToken(), "Chess");

    }

    @Test
    void createMultipleGames() throws ResponseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth = serviceUsers.registerUser(user);
        serviceGames.createGame(auth.authToken(), "Chess: the original");

        serviceGames.createGame(auth.authToken(), "Chess 2: Electric Boogaloo");

        serviceGames.createGame(auth.authToken(), "Chess 3: triple threat");

    }

    @Test
    void joinGame() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = serviceUsers.registerUser(user1);
        serviceGames.createGame(auth1.authToken(), "Chess");

        var user2 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var result2 = serviceUsers.registerUser(user2);
        serviceGames.joinGame(result2.authToken(),1, "BLACK");

    }
}
