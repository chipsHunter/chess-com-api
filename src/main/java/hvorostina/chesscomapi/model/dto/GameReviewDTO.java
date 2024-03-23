package hvorostina.chesscomapi.model.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.ZonedDateTime;

@Data
@Builder
public class GameReviewDTO {
    String timeClass;
    URL bestGameURL;
    String bestGameUuid;
    ZonedDateTime bestGameDate;
    int winCasesRecord;
    int lossCasesRecord;
    int drawCasesRecord;
}
