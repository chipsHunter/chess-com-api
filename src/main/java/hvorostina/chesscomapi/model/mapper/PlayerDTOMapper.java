package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.function.Function;

public class PlayerDTOMapper implements Function<Player, PlayerDTO> {
    @Override
    public PlayerDTO apply(Player player) {
        try {
            return PlayerDTO.builder()
                    .playerID(player.getPlayerID())
                    .userAccount((new URI("https://api.chess.com/pub/player/".concat(player.getUsername())).toURL()))
                    .username(player.getUsername())
                    .status(player.getStatus())
                    .country(player.getCountry())
                    .build();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
