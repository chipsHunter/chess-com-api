package hvorostina.chesscomapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GameReview {
    private static final int ALLOCATION_SIZE = 10;
    @Id
    @SequenceGenerator(name = "review_sequence_generator",
            sequenceName = "general_sequence",
            initialValue = 1, allocationSize = ALLOCATION_SIZE)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "review_sequence_generator")
    @Column(name = "id")
    private Integer id;
    @Column(name = "time_class")
    private String timeClass;
    @Column(name = "best_rating")
    private Integer bestRating = 0;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer winCasesRecord = 0;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer lossCasesRecord = 0;
    @Column(nullable = false, columnDefinition = "integer default 0")
    private Integer drawCasesRecord = 0;
    @ManyToOne
    private Player user;
    @ManyToOne
    private Game bestGame;
}
