package dataaccess;

import exception.ResponseException;
import model.UserData;

import java.util.HashMap;

public class UserDataAccess {
    HashMap<String, UserData> users  = new HashMap<String, UserData>();

    public UserData getUser(String username) throws ResponseException {
        var user = users.getOrDefault(username, null);
        if (user == null) {
            throw new ResponseException(500, "Error: (description of error)");
        }
        else {
            return user;
        }
    }

    public void addNewUser(UserData user) {
        users.put(user.username(), user);
    }

    public boolean alreadyRegistered(UserData user) {
        return users.containsKey(user.username());
    }

    public void clearAllUsers() throws ResponseException {
        if (!users.isEmpty()) {
            users.clear();
        }
        else {
            throw new ResponseException(500, "Error: (description of error)");
        }
    }
}
