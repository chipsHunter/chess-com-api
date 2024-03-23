package hvorostina.chesscomapi.model.dto;

import lombok.*;

import java.net.URL;
import java.time.ZonedDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GameDTOWithZonedTimeDate {
    URL gameURL;
    String uuid;
    ZonedDateTime endGameTimeDate;
    String timeClass;
    PlayerInGameDTO whitePlayer;
    PlayerInGameDTO blackPlayer;
}
