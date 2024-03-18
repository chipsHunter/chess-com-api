package hvorostina.chesscomapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;

@Entity
@Data
public class Game {
    @Id
    @SequenceGenerator(name = "game_sequence_generator",
            sequenceName = "general_sequence",
            initialValue = 1, allocationSize = 10)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "game_sequence_generator")
    Integer gameID;
    @JsonProperty("url")
    @Column(name = "game_url", nullable = false, unique = true)
    String gameURL;
    @JsonProperty("time_class")
    @Column(name = "time_class", nullable = false)
    String timeClass;
    @JsonProperty("end_time")
    @Column(name = "data", nullable = false)
    Timestamp data;
    @Column(name = "winner_id", nullable = false, columnDefinition = "integer default 0")
    Integer winnerID;
    @Column(name = "winner_side", nullable = false, columnDefinition = "varchar(255) default 'nobody'")
    String winnerSide;
    @Column(name = "white_rating", nullable = false, columnDefinition = "integer default 0")
    Integer whiteRating;
    @Column(name = "black_rating", nullable = false, columnDefinition = "integer default 0")
    Integer blackRating;
    @Column(name = "winner_rating", nullable = false, columnDefinition = "integer default 0")
    @ManyToMany(mappedBy = "games")     //target side
    Set<Player> players;
}
