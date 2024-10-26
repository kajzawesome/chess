package server;

import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import service.GameService;
import service.UserService;
import spark.*;

public class Server {

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

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private String createUser(Request req, Response res) {
        UserService  s = new UserService();
        var g = new Gson();
        var newUser = g.fromJson(
                req.body(), UserData.class);
        var createdUser  = s.registerUser(newUser);
        return g.toJson(createdUser);
    }

    private String login(Request req, Response res)  {
        UserService s = new UserService();
        var g = new Gson();
        var userLoggingIn = g.fromJson(req.body(), UserData.class);
        var loggedIn = s.login(userLoggingIn);
        return g.toJson(loggedIn);
    }

    private Object logout(Request req, Response res)  {
        UserService s = new UserService();
        var g = new Gson();
        var loggedOut = s.logout(req.headers().toString());
        return g.toJson(loggedOut);
    }

    private String createGame(Request req, Response res) {
        GameService game = new GameService();
        var g = new Gson();
        var newGame = g.fromJson(req.body(), String.class);
        var gameMade = game.createGame(req.headers().toString(), newGame);
        return g.toJson(gameMade);
    }

    private String listGames(Request req, Response res) {
        GameService game = new GameService();
        var g = new Gson();
        var games = game.listGames();
        return g.toJson(games);
    }

    private Object joinGame(Request req, Response res) {
        GameService game = new GameService();
        var g  = new Gson();
        var gameToJoin = g.fromJson(req.body(), GameData.class);
        var gameJoined = game.joinGame(req.headers().toString(), gameToJoin);
        return g.toJson(gameJoined);
    }

    private Object clear(Request req, Response res)  {
        //delete auth, then games, then users
        GameService game = new GameService();
        UserService user = new UserService();
        game.deleteGames();
        user.deleteAuth();
        user.deleteUsers();
        return "";
    }



}
