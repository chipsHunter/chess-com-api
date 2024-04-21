package hvorostina.chesscomapi.in_memory_cache;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class RequestGamesCacheServiceImpl {
    private final RequestCache<List<Game>> cache;
    private static final String PLAYER_GAMES_REQUEST = " games";
    private static final String GAME_BY_UUID_REQUEST = "Game with uuid ";
    public void saveByUser(final String username,
                           final List<Game> response) {
        String query = username + PLAYER_GAMES_REQUEST;
        cache.putQuery(query, response);
    }
    public void updateByUser(final String username,
                             final List<Game> response) {
        String query = username + PLAYER_GAMES_REQUEST;
        cache.updateQuery(query, response);
    }
    public void saveOrUpdateByUuid(final Game game) {
        List<Game> response = List.of(game);
        String query = GAME_BY_UUID_REQUEST + game.getUuid();
        cache.putQuery(query, response);
    }
    public void deleteAllByPlayer(final Player player) {
        String query = player.getUsername() + PLAYER_GAMES_REQUEST;
        cache.removeQuery(query);
        for (Game game: player.getGames()) {
            String gameQuery = GAME_BY_UUID_REQUEST + game.getUuid();
            cache.removeQuery(gameQuery);
        }
    }
    public void deleteByUuid(final String uuid) {
        String query = GAME_BY_UUID_REQUEST + uuid;
        cache.removeQuery(query);
    }
    public final void deleteAll() {
        cache.clear();
    }
    public Game getByUuid(final String uuid) {
        String query = GAME_BY_UUID_REQUEST + uuid;
        List<Game> response = cache.getResponse(query);
        if (response == null) {
            return null;
        }
        return response.get(0);
    }
}
