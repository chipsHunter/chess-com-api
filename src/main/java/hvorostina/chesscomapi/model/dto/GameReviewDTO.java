package hvorostina.chesscomapi.model.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.ZonedDateTime;

@Data
@Builder
public class GameReviewDTO {
    private String timeClass;
    private URL bestGameURL;
    private String bestGameUuid;
    private ZonedDateTime bestGameDate;
    private int winCasesRecord;
    private int lossCasesRecord;
    private int drawCasesRecord;
}
