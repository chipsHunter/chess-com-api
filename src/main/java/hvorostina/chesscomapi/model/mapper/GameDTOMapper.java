package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class GameDTOMapper implements Function<Game, GameDTO> {
    private final PlayersInGameDTOMapper playersInGameDTOMapper;
    @Override
    public GameDTO apply(Game game) {
        try {
            return GameDTO.builder()
                    .gameURL((new URI(game.getGameURL())).toURL())
                    .gameTimestamp(game.getData())
                    .gameData(LocalDateTime.ofInstant(game.getData().toInstant(), ZoneId.of("UTC+03:00")))
                    .uuid(game.getUUID())
                    .timeClass(game.getTimeClass())
                    .whitePlayer(playersInGameDTOMapper.apply(game).get(0))
                    .blackPlayer(playersInGameDTOMapper.apply(game).get(1))
                    .build();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new HttpClientErrorException(HttpStatus.URI_TOO_LONG);
        }
    }
}
