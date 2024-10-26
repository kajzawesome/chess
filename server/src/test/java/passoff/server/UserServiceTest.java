package passoff.server;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.exception.ResponseParseException;
import service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class UserServiceTest {
    static final UserService service = new UserService();

    @BeforeEach
    void clear() throws ResponseParseException {
        service.deleteAuth();
        service.deleteUsers();
    }

    @Test
    void registration() throws ResponseParseException {
        var user = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var result = service.registerUser(user);
        assertEquals(1,service.numValidUsers());
        assertTrue(service.validAuthToken(result.authToken()));
    }

    @Test
    void registerMultipleUsers() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var result1 = service.registerUser(user1);
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var result2 = service.registerUser(user2);
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var result3 = service.registerUser(user3);
        assertEquals(3,service.numRegisteredUsers());
        assertEquals(3,service.numValidUsers());
        assertTrue(service.validAuthToken(result1.authToken()));
        assertTrue(service.validAuthToken(result2.authToken()));
        assertTrue(service.validAuthToken(result3.authToken()));
    }

    @Test
    void logoutUser() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var result1 = service.registerUser(user1);
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var result2 = service.registerUser(user2);
        assertEquals(2,service.numValidUsers());
        assertTrue(service.validAuthToken(result1.authToken()));
        assertTrue(service.validAuthToken(result2.authToken()));
        service.logout(result1.authToken());
        assertEquals(1,service.numValidUsers());
    }

    @Test
    void loginUser() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var result1 = service.registerUser(user1);
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var result2 = service.registerUser(user2);
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var result3 = service.registerUser(user3);
        assertEquals(3,service.numValidUsers());
        assertTrue(service.validAuthToken(result1.authToken()));
        assertTrue(service.validAuthToken(result2.authToken()));
        assertTrue(service.validAuthToken(result3.authToken()));
        service.logout(result1.authToken());
        assertEquals(2,service.numValidUsers());
        var newResult = service.login(user1);
        assertEquals(3,service.numValidUsers());
        assertTrue(service.validAuthToken(newResult.authToken()));
    }

    @Test
    void logoutAll() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var result1 = service.registerUser(user1);
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var result2 = service.registerUser(user2);
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var result3 = service.registerUser(user3);
        assertEquals(3,service.numValidUsers());
        assertTrue(service.validAuthToken(result1.authToken()));
        assertTrue(service.validAuthToken(result2.authToken()));
        assertTrue(service.validAuthToken(result3.authToken()));
        service.logout(result1.authToken());
        service.logout(result2.authToken());
        service.logout(result3.authToken());
        assertEquals(0,service.numValidUsers());
    }

    @Test
    void completeReset() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var result1 = service.registerUser(user1);
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var result2 = service.registerUser(user2);
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
        var result3 = service.registerUser(user3);
        assertEquals(3,service.numValidUsers());
        assertTrue(service.validAuthToken(result1.authToken()));
        assertTrue(service.validAuthToken(result2.authToken()));
        assertTrue(service.validAuthToken(result3.authToken()));
        service.deleteAuth();
        service.deleteUsers();
        assertEquals(0,service.numValidUsers());
    }
}
