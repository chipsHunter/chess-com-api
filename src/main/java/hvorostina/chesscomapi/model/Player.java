package hvorostina.chesscomapi.model;

import hvorostina.chesscomapi.model.dto.PlayerDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.List;

@Entity
@Data
public class Player {
    @Id
    @Column(nullable = false, unique = true)
    Integer playerID;
    @Column(nullable = false, unique = true)
    String username;
    @Column(nullable = false)
    String country;
    @Column(nullable = false)
    String status;
    @ManyToMany     //owning side
    @LazyCollection(LazyCollectionOption.EXTRA)
    @JoinTable(
            name = "chess_matches",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id")
    )
    List<Game> games;
    @OneToMany(mappedBy = "user")
    List<GameReview> gameReviews;
}
