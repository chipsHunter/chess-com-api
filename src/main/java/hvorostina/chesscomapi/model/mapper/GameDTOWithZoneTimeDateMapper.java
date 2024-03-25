package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class GameDTOWithZoneTimeDateMapper implements Function<Game, GameDTOWithZonedTimeDate> {
    private final PlayersInGameDTOMapper playersInGameDTOMapper;
    @Override
    public GameDTOWithZonedTimeDate apply(Game game) {
        ZoneId zoneId = ZoneId.of("Europe/Minsk");
        ZonedDateTime actualDateTime = game.getData().atZone(zoneId);
        List<PlayerInGameDTO> players = playersInGameDTOMapper.apply(game);
        try {
            return GameDTOWithZonedTimeDate.builder()
                    .gameURL((new URI(game.getGameURL())).toURL())
                    .uuid(game.getUuid())
                    .endGameTimeDate(actualDateTime)
                    .timeClass(game.getTimeClass())
                    .whitePlayer(players.get(0))
                    .blackPlayer(players.get(1))
                    .build();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new HttpClientErrorException(HttpStatus.URI_TOO_LONG);
        }
    }
}
