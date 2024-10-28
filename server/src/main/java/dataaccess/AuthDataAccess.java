package dataaccess;

import exception.ResponseException;
import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDataAccess {
    HashMap<String, UserData> validAuth  = new HashMap<String, UserData>();

    public AuthData login(UserData user) {
        String userNewAuth = UUID.randomUUID().toString();
        validAuth.put(userNewAuth, user);
        return new AuthData(userNewAuth, user.username());
    }

    public void logout(String authToken) throws ResponseException {
        if (validAuth != null && validAuth.containsKey(authToken)) {
            validAuth.remove(authToken);
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }

    public UserData getUser(String auth) throws ResponseException {
        if (validAuth.containsKey(auth)) {
            return validAuth.get(auth);
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }

    public boolean validateAuth(String auth) {
        return validAuth.containsKey(auth);
    }

    public void clearAllAuths() throws ResponseException {
        if (!validAuth.isEmpty()) {
            validAuth.clear();
        }
    }

    public int loggedInUsers() { return validAuth.size();}
}
