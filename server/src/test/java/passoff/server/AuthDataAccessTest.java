package passoff.server;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthDataAccessTest {
    AuthDataAccess authData = new AuthDataAccess();

    @BeforeEach
    void clear() {
        authData.clearAllAuths();
    }

    @Test
    void login() throws DataAccessException {
        UserData user1 = new UserData("kajzawesome", "charlie", "kaj.jacobs@gmail.com");
        AuthData auth = authData.login(user1);
        assertTrue(authData.validateAuth(auth.authToken()));
    }
}
