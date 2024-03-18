package hvorostina.chesscomapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Data
public class GameReview {
    @Id
    @SequenceGenerator(name = "review_sequence_generator",
            sequenceName = "general_sequence",
            initialValue = 1, allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_sequence_generator")
    Integer gameReviewID;
    @Column(name = "game_type", nullable = false)
    String gameType;
    @JsonProperty("date")
    @Column(name = "best_game_date", nullable = false)
    Timestamp bestGameDate;
    @JsonProperty("game")
    @Column(name = "game_url", nullable = false)
    String bestGameURL;
    @JsonProperty("win")
    @Column(name = "wins", nullable = false, columnDefinition = "integer default 0")
    Integer winCasesRecord;
    @JsonProperty("loss")
    @Column(name = "loss", nullable = false, columnDefinition = "integer default 0")
    Integer lossCasesRecord;
    @JsonProperty("draw")
    @Column(name = "draws", nullable = false, columnDefinition = "integer default 0")
    Integer drawCasesRecord;
    @ManyToOne
    Player user;
}
