package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

@Component
public class GameReviewDTOMapper implements Function<GameReview, GameReviewDTO> {
    @Override
    public GameReviewDTO apply(GameReview gameReview) {
        try {
            return GameReviewDTO.builder()
                    .gameType(gameReview.getGameType())
                    .bestGameDate(LocalDateTime.ofInstant(gameReview.getBestGameDate().toInstant(), ZoneId.of("UTC+03:00")))
                    .bestGameURL((new URI(gameReview.getBestGameURL())).toURL())
                    .winCasesRecord(gameReview.getWinCasesRecord())
                    .lossCasesRecord(gameReview.getLossCasesRecord())
                    .drawCasesRecord(gameReview.getDrawCasesRecord())
                    .build();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
