package hvorostina.chesscomapi.model.dto;

import lombok.Builder;
import lombok.Data;

import java.net.URL;
import java.time.LocalDateTime;

@Data
@Builder
public class GameReviewDTO {
    String timeClass;
    LocalDateTime bestGameDate;
    URL bestGameURL;
    int winCasesRecord;
    int lossCasesRecord;
    int drawCasesRecord;
}
