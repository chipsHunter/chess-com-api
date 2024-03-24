package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GameReviewRepository extends JpaRepository<GameReview, Integer> {
    List<GameReview> findAllByUser(Player user);
}
