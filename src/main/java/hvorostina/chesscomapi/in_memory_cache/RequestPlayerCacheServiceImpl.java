package hvorostina.chesscomapi.in_memory_cache;

import hvorostina.chesscomapi.model.dto.PlayerDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RequestPlayerCacheServiceImpl {
    private static final String PLAYER_REQUEST = "Player ";
    private final RequestCache<PlayerDTO> cache;
    public void addOrUpdatePlayer(PlayerDTO player) {
        String query = PLAYER_REQUEST + player.getUsername();
        cache.putQuery(query, player);
    }
    public PlayerDTO getByUsername(String username) {
        String query = PLAYER_REQUEST + username;
        return cache.getResponse(query);
    }
    public void deletePlayer(String username) {
        String playerQuery = PLAYER_REQUEST + username;
        cache.removeQuery(playerQuery);
    }
}
