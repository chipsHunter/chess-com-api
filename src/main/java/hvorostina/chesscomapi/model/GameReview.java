package hvorostina.chesscomapi.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GameReview {
    @Id
    @SequenceGenerator(name = "review_sequence_generator",
            sequenceName = "general_sequence",
            initialValue = 1, allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_sequence_generator")
    @Column(name = "id")
    Integer id;
    @Column(nullable = false, columnDefinition = "integer default 0")
    Integer winCasesRecord = 0;
    @Column(nullable = false, columnDefinition = "integer default 0")
    Integer lossCasesRecord = 0;
    @Column(nullable = false, columnDefinition = "integer default 0")
    Integer drawCasesRecord = 0;
    @ManyToOne
    Player user;
    @ManyToOne
    Game bestGame;
}
