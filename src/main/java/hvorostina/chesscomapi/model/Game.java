package hvorostina.chesscomapi.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Game {
    @Id
    @SequenceGenerator(name = "game_sequence_generator",
            sequenceName = "general_sequence",
            initialValue = 1, allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_sequence_generator")
    @Column(name = "id", unique = true)
    Integer id;
    @Column(name = "game_url", nullable = false, unique = true)
    String gameURL;
    @Column(name = "uuid", nullable = false, unique = true)
    String uuid;
    @Column(name = "time_class", nullable = false)
    String timeClass;
    @Column(name = "data", nullable = false)
    LocalDateTime data;
    @Column(name = "winner_side", nullable = false, columnDefinition = "varchar(255) default 'nobody'")
    String winnerSide;
    @Column(name = "white_rating", nullable = false, columnDefinition = "integer default 0")
    Integer whiteRating;
    @Column(name = "black_rating", nullable = false, columnDefinition = "integer default 0")
    Integer blackRating;
    @Column(name = "game_result", nullable = false)
    String gameResult;
    @ManyToMany
    @JoinTable(
            name = "chess_matches",
            joinColumns = @JoinColumn(name = "game_id"),
            inverseJoinColumns = @JoinColumn(name = "player_id")
    )
    List<Player> players;
}
