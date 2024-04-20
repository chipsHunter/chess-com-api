package hvorostina.chesscomapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDTOWithDate {
    private URL gameURL;
    private String uuid;
    private ZonedDateTime endGameTimeDate;
    private String timeClass;
    private PlayerInGameDTO whitePlayer;
    private PlayerInGameDTO blackPlayer;

    public GameDTOWithDate(final GameDTO gameWithoutDate)
            throws URISyntaxException, MalformedURLException {
        ZoneId zoneId = ZoneId.of("Europe/Minsk");
        Instant timeInstant = Instant
                .ofEpochSecond(gameWithoutDate.getGameTimestamp());

        endGameTimeDate = ZonedDateTime.ofInstant(timeInstant, zoneId);
        gameURL = (new URI(gameWithoutDate.getGameURL())).toURL();
        uuid = gameWithoutDate.getUuid();
        timeClass = gameWithoutDate.getTimeClass();
        whitePlayer = gameWithoutDate.getWhitePlayer();
        blackPlayer = gameWithoutDate.getBlackPlayer();
    }
}
