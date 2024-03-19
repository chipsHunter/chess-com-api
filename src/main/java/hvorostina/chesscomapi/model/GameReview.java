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
    @Column(nullable = false)
    String gameType;
    @Column(nullable = false)
    Timestamp bestGameDate;
    @Column(nullable = false)
    String bestGameURL;
    @Column(nullable = false, columnDefinition = "integer default 0")
    Integer winCasesRecord;
    @Column(nullable = false, columnDefinition = "integer default 0")
    Integer lossCasesRecord;
    @Column(nullable = false, columnDefinition = "integer default 0")
    Integer drawCasesRecord;
    @ManyToOne
    Player user;
}
