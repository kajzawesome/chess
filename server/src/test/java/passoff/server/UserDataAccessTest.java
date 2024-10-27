package passoff.server;

import dataaccess.UserDataAccess;
import exception.ResponseException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDataAccessTest {
    UserDataAccess userData = new UserDataAccess();

    @BeforeEach
    void clear() {
        userData.clearAllUsers();
    }

    @Test
    void registerUser() {
        UserData user = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user);
        assertEquals(1,userData.numUsers());
    }

    @Test
    void registerUsers() {
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
    void getUserTest() throws ResponseException {
        UserData user = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user);
        assertEquals(1,userData.numUsers());
        assertEquals(user, userData.getUser("kajzawesome"));
    }

    @Test
    void clearAll() {
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
    void alreadyRegistered() {
        UserData user = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        userData.addNewUser(user);
        assertEquals(1,userData.numUsers());
        assertTrue(userData.alreadyRegistered("kajzawesome"));
    }
}
