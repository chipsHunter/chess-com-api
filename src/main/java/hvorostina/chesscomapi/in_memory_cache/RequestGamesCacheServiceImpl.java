package hvorostina.chesscomapi.in_memory_cache;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RequestGamesCacheServiceImpl {
    private final RequestCache<List<GameDTOWithZonedTimeDate>> cache;
    private static final String PLAYER_GAMES_REQUEST = " games";
    private static final String GAME_BY_UUID_REQUEST = "Game with uuid ";
    public void saveByUser(String username, List<GameDTOWithZonedTimeDate> response) {
        String query = username + PLAYER_GAMES_REQUEST;
        cache.putQuery(query, response);
    }
    public void updateByUser(String username, List<GameDTOWithZonedTimeDate> response) {
        String query = username + PLAYER_GAMES_REQUEST;
        cache.updateQuery(query, response);
    }
    public void saveOrUpdateByUuid(GameDTOWithZonedTimeDate game) {
        List<GameDTOWithZonedTimeDate> response = List.of(game);
        String query = GAME_BY_UUID_REQUEST + game.getUuid();
        cache.putQuery(query, response);
    }
    public void deleteAllByPlayer(Player player) {
        String query = player.getUsername() + PLAYER_GAMES_REQUEST;
        cache.removeQuery(query);
        for(Game game: player.getGames()) {
            String gameQuery = GAME_BY_UUID_REQUEST + game.getUuid();
            cache.removeQuery(gameQuery);
        }
    }
    public void deleteByUuid(String uuid) {
        String query = GAME_BY_UUID_REQUEST + uuid;
        cache.removeQuery(query);
    }
    public void deleteAll() {
        cache.clear();
    }
    public GameDTOWithZonedTimeDate getByUuid(String uuid) {
        String query = GAME_BY_UUID_REQUEST + uuid;
        return cache.getResponse(query).get(0);
    }
}
