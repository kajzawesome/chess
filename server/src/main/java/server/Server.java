package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import exception.ResponseException;
import model.GameData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {
    UserService  user = new UserService();
    GameService game = new GameService();

    public int run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.post("/user", this::createUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.delete("/db", this::clear);
        Spark.exception(ResponseException.class, this::exceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String createUser(Request req, Response res) throws ResponseException {
        var g = new Gson();
        var newUser = g.fromJson(
                req.body(), UserData.class);
        var createdUser  = user.registerUser(newUser);
        game.updateAuthData(user.getAuthData());
        return g.toJson(createdUser);
    }

    private String login(Request req, Response res) throws ResponseException {
        var g = new Gson();
        var userLoggingIn = g.fromJson(req.body(), UserData.class);
        var loggedIn = user.login(userLoggingIn);
        game.updateAuthData(user.getAuthData());
        return g.toJson(loggedIn);
    }

    private Object logout(Request req, Response res) throws ResponseException {
        var g = new Gson();
        var loggedOut = user.logout(req.headers().toString());
        game.updateAuthData(user.getAuthData());
        return g.toJson(loggedOut);
    }

    private String createGame(Request req, Response res) throws DataAccessException, ResponseException {
        var g = new Gson();
        var newGame = g.fromJson(req.body(), GameData.class);
        var gameMade = game.createGame(req.headers().toString(), newGame.gameName());
        return g.toJson(gameMade);
    }

    private String listGames(Request req, Response res) throws DataAccessException, ResponseException {
        var g = new Gson();
        var allGames = game.listGames(req.headers().toString());
        return g.toJson(allGames);
    }

    private Object joinGame(Request req, Response res) throws ResponseException {
        var g  = new Gson();
        record gameJoinInfo(int gameID, String playerColor) {}
        var gameToJoin = g.fromJson(req.body(), gameJoinInfo.class);
        var gameJoined = game.joinGame(req.headers().toString(), gameToJoin.gameID, gameToJoin.playerColor); //how to separate ID num from team color
        return g.toJson(gameJoined);
    }

    private Object clear(Request req, Response res) throws ResponseException {
        game.deleteGames();
        user.deleteAuth();
        user.deleteUsers();
        game.updateAuthData(null);
        return "";
    }

    private void exceptionHandler(ResponseException exception, Request req, Response res) {
        res.status(exception.StatusCode());
        record ExceptionMessage(String message) {}
        res.body(new Gson().toJson(new ExceptionMessage(exception.getMessage())));
    }

}