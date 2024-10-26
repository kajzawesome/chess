package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDataAccess {
    HashMap<String, UserData> validAuth  = new HashMap<String, UserData>();

    public AuthData login(UserData user) throws DataAccessException {
        if (!alreadyLoggedIn(user)) {
            String userNewAuth = UUID.randomUUID().toString();
            validAuth.put(userNewAuth, user);
            return new AuthData(userNewAuth, user.username());
        }
        else {
            throw new DataAccessException("Already logged in");
        }
    }

    public boolean alreadyLoggedIn(UserData user) {
        return validAuth.containsValue(user);
    }

    public boolean alreadyLoggedIn(String auth) {
        return validAuth.containsKey(auth);
    }

    public void logout(String authToken) {
        validAuth.remove(authToken);
    }

    public UserData getUser(String auth) { return validAuth.get(auth);}

    public boolean validateAuth(String auth) {
        return validAuth.containsKey(auth);
    }
    public void clearAllAuths() { validAuth.clear();}
}
