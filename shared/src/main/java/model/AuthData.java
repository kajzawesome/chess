package model;

public record AuthData(String authToken, String username) {

    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getUser() {
        return username;
    }

    public String getAuth() {
        return authToken;
    }
}
