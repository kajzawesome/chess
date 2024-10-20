package service;

import model.AuthData;
import model.UserData;

public class UserService {
    public AuthData registerUser(UserData user) {
        return new AuthData("hi", "kajzawesome");
    }

    public AuthData login(UserData user) {
        return new AuthData("hi", "kajzawesome");
    }

    public void logout(AuthData auth) {

    }
}
