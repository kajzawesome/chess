package model;

import com.google.gson.Gson;

public record AuthData(String authToken, String username) {

    public String getUser() {
        return username;
    }

    public String getAuth() {
        return authToken;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}
