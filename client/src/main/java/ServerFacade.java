import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import exception.ResponseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

public class ServerFacade {

    private final String serverUrl;
    private String auth;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void setAuth(String auth) { this.auth = auth;}

    //register each end point

    public AuthData createUser(UserData user) throws ResponseException {
        var path = "/user";
        AuthData request = this.makeRequest("POST", path, user, AuthData.class);
        setAuth(request.authToken());
        return request;
    }

    public AuthData login(UserData user) throws ResponseException {
        var path = "/session";
        AuthData request = this.makeRequest("POST", path, user, AuthData.class);
        setAuth(request.authToken());
        return request;
    }

    public void logout() throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null);
    }

    public String listGames() throws ResponseException {
        var path = "/game";
        record listGamesResponse(GameData[] gameData){}
        var response = this.makeRequest("GET", path, null, listGamesResponse.class);
        return Arrays.toString(response.gameData());
    }

    public int createGame(String gameName) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, gameName, int.class);
    }

    public void joinGame(int gameID, String teamColor) throws ResponseException {
        var path = "/game";
        record gameToJoin(int gameID, String teamColor) {}
        this.makeRequest("PUT", path, new gameToJoin(gameID, teamColor), int.class);
    }

    //helper functions

    public ChessGame getGame(int gameID) throws ResponseException {
        var path = "/game";
        var response = this.makeRequest("GET", path, null, GameData.class);
        return response.game();
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            http.writeHEar(auth);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}