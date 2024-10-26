package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDataAccess {
    HashMap<String, UserData> users  = new HashMap<String, UserData>();

    public UserData getUser(String username) {
        return users.getOrDefault(username, null);
    }

    public void addNewUser(UserData user) {
        users.put(user.username(), user);
    }


}
