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

    }

    @Test
    void registerMultipleUsers() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");

    }

    @Test
    void logoutUser() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");

    }

    @Test
    void loginUser() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");

    }

    @Test
    void logoutAll() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");

    }

    @Test
    void completeReset() throws ResponseParseException {
        var user1 = new UserData("kajzawesome","charles","kaj.jacobs@gmail.com");
        var user2 = new UserData("Karamel","yellow","A.Malolo@gmail.com");
        var user3 = new UserData("Gundybuckets","xDefiant","gundy@gmail.com");
    }
}
