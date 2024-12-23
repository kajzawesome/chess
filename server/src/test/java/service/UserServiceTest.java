package service;

import dataaccess.DataAccessException;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;


public class UserServiceTest {
    UserService service = new UserService();

    public UserServiceTest() throws ResponseException, DataAccessException {
    }

    @BeforeEach
    void clear() throws ResponseException, SQLException {
        service.deleteAuth();
        service.deleteUsers();
    }

    @Test
    void registration() throws ResponseException, DataAccessException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth = service.registerUser(user);
        assertEquals(1, service.numRegistered());
        assertTrue(service.validateAuth(auth.authToken()));
        assertEquals(user.username(), service.getUser(user.username()).username());
    }

    @Test
    void registerMultipleUsers() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = service.registerUser(user1);
        assertEquals(1, service.numRegistered());
        assertTrue(service.validateAuth(auth1.authToken()));
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var auth2 = service.registerUser(user2);
        assertEquals(2, service.numRegistered());
        assertTrue(service.validateAuth(auth2.authToken()));
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var auth3 = service.registerUser(user3);
        assertEquals(3, service.numRegistered());
        assertTrue(service.validateAuth(auth3.authToken()));
    }

    @Test
    void logoutUser() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = service.registerUser(user1);
        assertEquals(1, service.numRegistered());
        assertTrue(service.validateAuth(auth1.authToken()));
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var auth2 = service.registerUser(user2);
        assertEquals(2, service.numRegistered());
        assertTrue(service.validateAuth(auth2.authToken()));
        assertEquals(2, service.loggedInUsers());
        service.logout(auth2.authToken());
        assertEquals(1, service.loggedInUsers());
    }

    @Test
    void loginUnregisteredUser() throws ResponseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        assertEquals(0, service.numRegistered());
        ResponseException exception = assertThrows(ResponseException.class, () -> service.login(user));
        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());
    }

    @Test
    void loginUser() throws ResponseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        assertEquals(0, service.numRegistered());
        var auth = service.registerUser(user);
        assertTrue(service.validateAuth(auth.authToken()));
        assertEquals(1, service.loggedInUsers());
    }

    @Test
    void logoutAll() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var auth1 = service.registerUser(user1);
        assertTrue(service.validateAuth(auth1.authToken()));
        var auth2 = service.registerUser(user2);
        assertTrue(service.validateAuth(auth2.authToken()));
        var auth3 = service.registerUser(user3);
        assertTrue(service.validateAuth(auth3.authToken()));
        assertEquals(3, service.numRegistered());
        assertEquals(3, service.loggedInUsers());
        service.logout(auth1.authToken());
        assertEquals(2, service.loggedInUsers());
        assertEquals(3, service.numRegistered());
        service.logout(auth2.authToken());
        service.logout(auth3.authToken());
        assertEquals(0, service.loggedInUsers());
        assertEquals(3, service.numRegistered());
    }

    @Test
    void logoutBadAuth() throws ResponseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var auth1 = service.registerUser(user1);
        assertTrue(service.validateAuth(auth1.authToken()));
        ResponseException exception = assertThrows(ResponseException.class, () -> service.logout("hi there"));
        assertEquals("Error: unauthorized", exception.getMessage());
        assertEquals(401, exception.StatusCode());
    }
}
