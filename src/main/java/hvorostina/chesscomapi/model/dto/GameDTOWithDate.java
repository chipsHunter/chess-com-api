package hvorostina.chesscomapi.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
