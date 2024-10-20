package service;

import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    public AuthData registerUser(UserData user) {
        return new AuthData(UUID.randomUUID().toString(), "kajzawesome");
    }

    public AuthData login(UserData user) {
        return new AuthData(UUID.randomUUID().toString(), "kajzawesome");
    }

    public void logout(AuthData auth) {

    }
}
