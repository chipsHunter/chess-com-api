package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.function.Function;

@Component
public final class GameReviewDTOMapper implements
        Function<GameReview, GameReviewDTO> {
    @Override
    public GameReviewDTO apply(final GameReview gameReview) {
        ZoneId zoneId = ZoneId.of("Europe/Minsk");
        ZonedDateTime actualDateTime = gameReview.getBestGame()
                .getData().atZone(zoneId);
        try {
            return GameReviewDTO.builder()
                    .timeClass(gameReview.getTimeClass())
                    .bestGameURL((new URI(gameReview
                            .getBestGame()
                            .getGameURL()))
                            .toURL())
                    .bestGameUuid(gameReview.getBestGame().getUuid())
                    .bestGameDate(actualDateTime)
                    .winCasesRecord(gameReview.getWinCasesRecord())
                    .lossCasesRecord(gameReview.getLossCasesRecord())
                    .drawCasesRecord(gameReview.getDrawCasesRecord())
                    .build();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new HttpClientErrorException(HttpStatus.URI_TOO_LONG);
        }
    }
}
