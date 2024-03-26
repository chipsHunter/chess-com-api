package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameReviewRepository extends JpaRepository<GameReview, Integer> {
    List<GameReview> findAllByUser(Player user);
    void deleteAllByUser(Player player);
}
