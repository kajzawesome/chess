package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDataAccessSQLTest {
    AuthDataAccessSQL authData = new AuthDataAccessSQL();

    public AuthDataAccessSQLTest() throws ResponseException, DataAccessException {
    }

    @BeforeEach
    public void clear() throws ResponseException, SQLException {
        authData.clearAllAuths();
    }

    @Test
    public void login() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertTrue(authData.validateAuth(auth.authToken()));
    }

    @Test
    public void logins() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth1 = authData.login(user1);
        UserData user2 = new UserData("Gundybuckets", "xDefiant", "gundy@gmail.com");
        AuthData auth2 = authData.login(user2);
        UserData user3 = new UserData("Karamel", "yellow", "karamel@gmail.com");
        AuthData auth3 = authData.login(user3);
        assertTrue(authData.validateAuth(auth1.authToken()));
        assertTrue(authData.validateAuth(auth2.authToken()));
        assertTrue(authData.validateAuth(auth3.authToken()));
        assertNotEquals(auth1.authToken(), auth2.authToken());
        assertNotEquals(auth1.authToken(), auth3.authToken());
        assertNotEquals(auth2.authToken(), auth3.authToken());
    }

    @Test
    public void badLogin() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertTrue(authData.validateAuth(auth.authToken()));
        assertNotEquals("12345", auth.authToken());
    }

    @Test
    public void getRequest() throws ResponseException, SQLException, DataAccessException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertEquals(authData.getUser(auth.authToken()).username(), user1.username());
        assertEquals(1, authData.loggedInUsers());
    }

    @Test
    public void logout() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertTrue(authData.validateAuth(auth.authToken()));
        authData.logout(auth.authToken());
        assertFalse(authData.validateAuth(auth.authToken()));
        assertEquals(0, authData.loggedInUsers());
    }

    @Test
    public void badLogout() {
        ResponseException exception = assertThrows(ResponseException.class, () -> authData.logout("auth"));
        assertEquals("Error: (description of error)", exception.getMessage());
        assertEquals(500, exception.StatusCode());
    }

    @Test
    public void clearAll() throws ResponseException, SQLException {
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
