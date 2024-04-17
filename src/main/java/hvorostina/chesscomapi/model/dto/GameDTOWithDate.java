package hvorostina.chesscomapi.model.dto;

import lombok.*;

import java.net.URL;
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
}
