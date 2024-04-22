package hvorostina.chesscomapi.in_memory_cache;

import hvorostina.chesscomapi.model.Player;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RequestPlayerCacheServiceImpl {
    private static final String PLAYER_REQUEST = "Player ";
    private final RequestCache<Player> cache;
    public void saveOrUpdate(final Player player) {
        String query = PLAYER_REQUEST + player.getUsername();
        cache.putQuery(query, player);
    }
    public Player getByUsername(final String username) {
        String query = PLAYER_REQUEST + username;
        return cache.getResponse(query);
    }
    public void delete(final String username) {
        String playerQuery = PLAYER_REQUEST + username;
        cache.removeQuery(playerQuery);
    }
}
