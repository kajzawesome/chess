package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        facade.clear();
    }

    private String registerPlayer() throws ResponseException {
        return facade.register("player1", "password", "p1@email.com").authToken();
    }

    @Test
    void registerSuccess() throws ResponseException {
        var auth = facade.register("player1", "password", "p1@email.com");
        assertEquals("player1", auth.username());
        assertNotNull(auth.authToken());
        assertTrue(auth.authToken().length() > 10);
    }

    @Test
    void registerDuplicateFails() throws ResponseException {
        registerPlayer();
        var ex = assertThrows(ResponseException.class,
                () -> facade.register("player1", "other", "x@email.com"));
        assertEquals(403, ex.StatusCode());
    }

    @Test
    void loginSuccess() throws ResponseException {
        registerPlayer();
        var auth = facade.login("player1", "password");
        assertEquals("player1", auth.username());
        assertNotNull(auth.authToken());
    }

    @Test
    void loginWrongPasswordFails() throws ResponseException {
        registerPlayer();
        var ex = assertThrows(ResponseException.class, () -> facade.login("player1", "wrong"));
        assertEquals(401, ex.StatusCode());
    }

    @Test
    void logoutSuccess() throws ResponseException {
        var auth = registerPlayer();
        assertDoesNotThrow(() -> facade.logout(auth));
        // the token no longer works afterward
        assertThrows(ResponseException.class, () -> facade.listGames(auth));
    }

    @Test
    void logoutBadTokenFails() {
        assertThrows(ResponseException.class, () -> facade.logout("not-a-real-token"));
    }

    @Test
    void createGameSuccess() throws ResponseException {
        var auth = registerPlayer();
        int gameID = facade.createGame(auth, "my game");
        assertTrue(gameID > 0);
    }

    @Test
    void createGameUnauthorizedFails() {
        var ex = assertThrows(ResponseException.class, () -> facade.createGame("bad-token", "my game"));
        assertEquals(401, ex.StatusCode());
    }

    @Test
    void listGamesSuccess() throws ResponseException {
        var auth = registerPlayer();
        assertTrue(facade.listGames(auth).isEmpty());
        facade.createGame(auth, "game one");
        facade.createGame(auth, "game two");
        var games = facade.listGames(auth);
        assertEquals(2, games.size());
        assertNotNull(games.get(0).game());
    }

    @Test
    void listGamesUnauthorizedFails() {
        var ex = assertThrows(ResponseException.class, () -> facade.listGames("bad-token"));
        assertEquals(401, ex.StatusCode());
    }

    @Test
    void joinGameSuccess() throws ResponseException {
        var auth = registerPlayer();
        int gameID = facade.createGame(auth, "my game");
        facade.joinGame(auth, gameID, "WHITE");
        var game = facade.listGames(auth).get(0);
        assertEquals("player1", game.whiteUsername());
        assertNull(game.blackUsername());
    }

    @Test
    void joinGameTakenColorFails() throws ResponseException {
        var auth = registerPlayer();
        int gameID = facade.createGame(auth, "my game");
        facade.joinGame(auth, gameID, "WHITE");
        var other = facade.register("player2", "password", "p2@email.com").authToken();
        var ex = assertThrows(ResponseException.class, () -> facade.joinGame(other, gameID, "WHITE"));
        assertEquals(403, ex.StatusCode());
    }

    @Test
    void clearSuccess() throws ResponseException {
        var auth = registerPlayer();
        facade.createGame(auth, "my game");
        facade.clear();
        // user is gone, so login fails
        assertThrows(ResponseException.class, () -> facade.login("player1", "password"));
    }
}
