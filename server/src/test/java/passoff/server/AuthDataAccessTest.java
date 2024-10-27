package passoff.server;

import dataaccess.AuthDataAccess;
import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDataAccessTest {
    AuthDataAccess authData = new AuthDataAccess();

    @BeforeEach
    void clear() throws ResponseException {
        authData.clearAllAuths();
    }

    @Test
    void login() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertTrue(authData.validateAuth(auth.authToken()));
    }

    @Test
    void getRequest() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertEquals(authData.getUser(auth.authToken()), user1);
    }

    @Test
    void logout() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertTrue(authData.validateAuth(auth.authToken()));
        authData.logout(auth.authToken());
        assertEquals(0, authData.loggedInUsers());
    }

    @Test
    void badLogout() {
        ResponseException exception = assertThrows(ResponseException.class, () -> authData.logout("auth"));
        assertEquals("Error: (description of error)", exception.getMessage());
        assertEquals(500, exception.StatusCode());
    }

    @Test
    void clearAll() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth1 = authData.login(user1);
        UserData user2 = new UserData("Gundybuckets", "xDefiant", "gundy@gmail.com");
        AuthData auth2 = authData.login(user2);
        UserData user3 = new UserData("Karamel", "yellow", "karamel@gmail.com");
        AuthData auth3 = authData.login(user3);
        assertTrue(authData.validateAuth(auth1.authToken()));
        assertTrue(authData.validateAuth(auth2.authToken()));
        assertTrue(authData.validateAuth(auth3.authToken()));
        assertEquals(3,authData.loggedInUsers());
        authData.clearAllAuths();
        assertEquals(0,authData.loggedInUsers());
    }
}
