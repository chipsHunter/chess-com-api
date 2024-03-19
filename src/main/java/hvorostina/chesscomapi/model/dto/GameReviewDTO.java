package hvorostina.chesscomapi.model.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
public class GameReviewDTO {
    String gameType;
    LocalDateTime bestGameDate;
    URL bestGameURL;
    int winCasesRecord;
    int lossCasesRecord;
    int drawCasesRecord;
}
