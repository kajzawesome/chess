package dataacess;

import dataaccess.DataAccessException;
import dataaccess.UserDataAccessSQL;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDataAccessSQLTest {
    UserDataAccessSQL userData = new UserDataAccessSQL();

    public UserDataAccessSQLTest() throws ResponseException, DataAccessException {
    }

    @BeforeEach
    void clear() throws ResponseException {
        userData.clearAllUsers();
    }

    @Test
    void registerUser() throws ResponseException {
        UserData user = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user);
        assertEquals(1,userData.numUsers());
    }

    @Test
    void registerUsers() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user1);
        assertEquals(1,userData.numUsers());
        UserData user2 = new UserData("Gundybuckets", "xDefiant", "gundy@gmail.com");
        userData.addNewUser(user2);
        assertEquals(2,userData.numUsers());
        UserData user3 = new UserData("Karamel", "yellow", "karamel@gmail.com");
        userData.addNewUser(user3);
        assertEquals(3,userData.numUsers());
    }

    @Test
    void badRegister() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user1);
        assertEquals(1,userData.numUsers());
        UserData user2 = new UserData(null, "charlie", "kaj.jacobs@gmail.com");
        ResponseException exception = assertThrows(ResponseException.class, () -> userData.addNewUser(user2));
        assertEquals("unable to update database: INSERT INTO Users (Username, Password, Email, User) VALUES (?, ?, ?, ?), Column 'Username' cannot be null", exception.getMessage());
        assertEquals(500, exception.StatusCode());
    }

    @Test
    void getUserTest() throws ResponseException, DataAccessException {
        UserData user = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user);
        assertEquals(1,userData.numUsers());
        assertEquals(user.username(), userData.getUser("kajzawesome").username());
        assertEquals(user.email(), userData.getUser("kajzawesome").email());
    }

    @Test
    void badGetUserTest() throws ResponseException, DataAccessException {
        UserData user = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user);
        assertEquals(1,userData.numUsers());
        assertEquals(user.username(), userData.getUser("kajzawesome").username());
        assertEquals(user.email(), userData.getUser("kajzawesome").email());
        var exception = userData.getUser("hi");
        assertNull(exception);
    }

    @Test
    void clearAll() throws ResponseException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user1);
        UserData user2 = new UserData("Gundybuckets", "xDefiant", "gundy@gmail.com");
        userData.addNewUser(user2);
        UserData user3 = new UserData("Karamel", "yellow", "karamel@gmail.com");
        userData.addNewUser(user3);
        userData.clearAllUsers();
        assertEquals(0,userData.numUsers());
    }

    @Test
    void alreadyRegistered() throws ResponseException {
        UserData user = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user);
        assertEquals(1,userData.numUsers());
        assertTrue(userData.alreadyRegistered("kajzawesome"));
    }

    @Test
    void notRegistered() {
        assertEquals(0,userData.numUsers());
        assertFalse(userData.alreadyRegistered("kajzawesome"));
    }
}
