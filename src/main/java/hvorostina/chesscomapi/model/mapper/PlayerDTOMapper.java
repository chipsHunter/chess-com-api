package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Function;

@Component
public class PlayerDTOMapper implements Function<Player, PlayerDTO> {
    @Override
    public PlayerDTO apply(final Player player) {
        try {
            return PlayerDTO.builder()
                    .id(player.getId())
                    .userAccount((new URI("https://api.chess.com/pub/player/"
                            .concat(player.getUsername()))).toURL())
                    .username(player.getUsername())
                    .status(player.getStatus())
                    .country(player.getCountry())
                    .build();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new HttpClientErrorException(HttpStatus.URI_TOO_LONG);
        }
    }
}
