package passoff.server;

import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.exception.ResponseParseException;
import service.GameService;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GameServiceTest {
    static final GameService serviceGames = new GameService();
    static final UserService serviceUsers = new UserService();

    @BeforeEach
    void clear() throws ResponseParseException {
        serviceGames.deleteGames();
    }

    @Test
    void createGame() throws ResponseParseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth = serviceUsers.registerUser(user);
        serviceGames.createGame(auth.authToken(), "Chess");
        assertEquals(1,serviceGames.numGames());
    }

    @Test
    void createGames() throws ResponseParseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth = serviceUsers.registerUser(user);
        serviceGames.createGame(auth.authToken(), "Chess: the original");
        assertEquals(1,serviceGames.numGames());
        serviceGames.createGame(auth.authToken(), "Chess 2: Electric Boogaloo");
        assertEquals(2,serviceGames.numGames());
        serviceGames.createGame(auth.authToken(), "Chess 3: triple threat");
        assertEquals(3,serviceGames.numGames());
    }

    @Test
    void joinGame() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = serviceUsers.registerUser(user1);
        serviceGames.createGame(auth1.authToken(), "Chess", 1);
        assertEquals(1,serviceGames.numGames());
        var user2 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var result2 = serviceUsers.registerUser(user2);
        serviceGames.joinGame(result2.authToken(),1);
        var game = serviceGames.getGame(1);
        assertEquals(game.whiteUsername(), "kajzawesome");
        assertEquals(game.blackUsername(), "Gundybuckets");
    }
}
