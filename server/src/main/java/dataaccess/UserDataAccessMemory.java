package dataaccess;

import exception.ResponseException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.HashMap;

public class UserDataAccessMemory {
    HashMap<String, UserData> users  = new HashMap<String, UserData>();

    public UserData getUser(String username) throws ResponseException {
        var user = users.getOrDefault(username, null);
        if (user == null) {
            throw new ResponseException(401, "Error: (description of error)");
        }
        else {
            return user;
        }
    }

    public void addNewUser(UserData user) {
        UserData databaseUser = new UserData(user.username(), BCrypt.hashpw(user.password(), BCrypt.gensalt()), user.email());
        users.put(user.username(), databaseUser);
    }

    public boolean alreadyRegistered(String username) {
        return users.containsKey(username);
    }

    public void clearAllUsers() {
        if (!users.isEmpty()) {
            users.clear();
        }
    }

    public int numUsers() {
        return users.size();
    }
}
