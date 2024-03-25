package hvorostina.chesscomapi.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
public class Player {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    Integer id;
    @Column(nullable = false, unique = true)
    String username;
    @Column(nullable = false)
    String country;
    @Column(nullable = false)
    String status;
    @ManyToMany     //owning side
    @JoinTable(
            name = "chess_matches",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    List<Game> games;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    List<GameReview> gameReviews;
}
