package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import hvorostina.chesscomapi.model.dto.PlayerWithGamesDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class PlayerWithGamesDTOMapper implements
        Function<Player, PlayerWithGamesDTO> {
    private final GameDTOWithDateMapper gameDTOWithDateMapper;
    @Override
    public PlayerWithGamesDTO apply(final Player player) {
        List<GameDTOWithDate> userGames = player.getGames().stream()
                .map(gameDTOWithDateMapper)
                .toList();
        try {
            return PlayerWithGamesDTO.builder()
                    .userAccount((new URI("https://api.chess.com/pub/player/"
                            .concat(player.getUsername()))).toURL())
                    .username(player.getUsername())
                    .games(userGames)
                    .build();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new HttpClientErrorException(HttpStatus.URI_TOO_LONG);
        }
    }
}
