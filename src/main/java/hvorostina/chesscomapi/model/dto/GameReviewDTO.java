package hvorostina.chesscomapi.model.dto;

import lombok.Data;

import java.net.URL;
import java.util.Date;

@Data
public class GameReviewDTO {
    String gameType;
    Date bestGameDate;
    URL bestGameURL;
    int winCasesRecord;
    int lossCasesRecord;
    int drawCasesRecord;
}
