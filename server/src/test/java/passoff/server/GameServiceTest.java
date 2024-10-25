package passoff.server;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import passoff.exception.ResponseParseException;
import service.GameService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class GameServiceTest {
    static final GameService service = new GameService();

    @BeforeEach
    void clear() throws ResponseParseException {
        service.deleteGames();
    }


}
