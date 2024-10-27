package service;

import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GameServiceTest {
    static final GameService serviceGames = new GameService();
    static final UserService serviceUsers = new UserService();

    @BeforeEach
    void clear() throws ResponseException {
        serviceGames.deleteGames();
        serviceUsers.deleteUsers();
        serviceUsers.deleteAuth();
    }

    @Test
    void createGame() throws ResponseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth = serviceUsers.registerUser(user);
        serviceGames.updateAuthData(serviceUsers.getAuthData());
        var gameID = serviceGames.createGame(auth.authToken(), "Chess");
        assertEquals(1, serviceGames.numGames());
        assertEquals("Chess", serviceGames.getGame(gameID).gameName());
    }

    @Test
    void createMultipleGames() throws ResponseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth = serviceUsers.registerUser(user);
        serviceGames.updateAuthData(serviceUsers.getAuthData());
        serviceGames.createGame(auth.authToken(), "Chess: the original");
        assertEquals(1, serviceGames.numGames());
        serviceGames.createGame(auth.authToken(), "Chess 2: Electric Boogaloo");
        assertEquals(2, serviceGames.numGames());
        serviceGames.createGame(auth.authToken(), "Chess 3: triple threat");
        assertEquals(3, serviceGames.numGames());
    }

    @Test
    void joinGame() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = serviceUsers.registerUser(user1);
        serviceGames.updateAuthData(serviceUsers.getAuthData());
        var gameID = serviceGames.createGame(auth1.authToken(), "Chess");
        assertEquals(1, serviceGames.numGames());
        var user2 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var auth2 = serviceUsers.registerUser(user2);
        serviceGames.joinGame(auth2.authToken(),gameID, "BLACK");
        assertEquals("Gundybuckets", serviceGames.getGame(gameID).blackUsername());
    }

    @Test
    void doubleJoinGame() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = serviceUsers.registerUser(user1);
        serviceGames.updateAuthData(serviceUsers.getAuthData());
        var gameID = serviceGames.createGame(auth1.authToken(), "Chess");
        assertEquals(1, serviceGames.numGames());
        var user2 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var auth2 = serviceUsers.registerUser(user2);
        serviceGames.joinGame(auth1.authToken(),gameID, "WHITE");
        serviceGames.joinGame(auth2.authToken(),gameID, "BLACK");
        assertEquals("Gundybuckets", serviceGames.getGame(gameID).blackUsername());
        assertEquals("kajzawesome", serviceGames.getGame(gameID).whiteUsername());
    }

    @Test
    void gameAlreadyFull() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = serviceUsers.registerUser(user1);
        serviceGames.updateAuthData(serviceUsers.getAuthData());
        var gameID = serviceGames.createGame(auth1.authToken(), "Chess");
        assertEquals(1, serviceGames.numGames());
        var user2 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var auth2 = serviceUsers.registerUser(user2);
        serviceGames.joinGame(auth1.authToken(),gameID, "WHITE");
        serviceGames.joinGame(auth2.authToken(),gameID, "BLACK");
        assertEquals("Gundybuckets", serviceGames.getGame(gameID).blackUsername());
        assertEquals("kajzawesome", serviceGames.getGame(gameID).whiteUsername());
        var user3 = new UserData("karamel","yellow","shadowdog@gmail.com");
        var auth3 = serviceUsers.registerUser(user3);
        ResponseException exception = assertThrows(ResponseException.class, () -> serviceGames.joinGame(auth3.authToken(),gameID, "BLACK"));
        assertEquals("Error: bad request", exception.getMessage());
        assertEquals(400, exception.StatusCode());
    }
}
