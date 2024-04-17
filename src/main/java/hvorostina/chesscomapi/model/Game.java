package hvorostina.chesscomapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Game {
    private static final int ALLOCATION_SIZE = 10;
    @Id
    @SequenceGenerator(name = "game_sequence_generator",
            sequenceName = "general_sequence",
            initialValue = 1, allocationSize = ALLOCATION_SIZE)
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "game_sequence_generator")
    @Column(name = "id", unique = true)
    private Integer id;
    @Column(name = "game_url", nullable = false, unique = true)
    private String gameURL;
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;
    @Column(name = "time_class", nullable = false)
    private String timeClass;
    @Column(name = "data", nullable = false)
    private LocalDateTime data;
    @Column(name = "winner_side", nullable = false,
            columnDefinition = "varchar(255) default 'nobody'")
    private String winnerSide;
    @Column(name = "white_rating", nullable = false,
            columnDefinition = "integer default 0")
    private Integer whiteRating;
    @Column(name = "black_rating", nullable = false,
            columnDefinition = "integer default 0")
    private Integer blackRating;
    @Column(name = "game_result", nullable = false)
    private String gameResult;
    @ManyToMany
    @JoinTable(
            name = "chess_matches",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    private List<Player> players;
}
