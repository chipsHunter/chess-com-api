package hvorostina.chesscomapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Player {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private Integer id;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String status;
    @ManyToMany
    @JoinTable(
            name = "chess_matches",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    private List<Game> games;
    @OneToMany(cascade = {CascadeType.REMOVE, CascadeType.DETACH},
            mappedBy = "user")
    private List<GameReview> gameReviews;
}
