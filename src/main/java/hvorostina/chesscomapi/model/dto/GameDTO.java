package hvorostina.chesscomapi.model.dto;

import lombok.Data;
import org.springframework.lang.NonNull;

@Data
public class GameDTO {
    String gameURL;
    Data gameData;
    String timeClass;
    String whitePlayerUsername;
    int whitePlayerRating;
    String blackPlayerUsername;
    int blackPlayerRating;
    String wonSide;
}
