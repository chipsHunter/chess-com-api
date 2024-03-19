package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameReviewRepository extends JpaRepository<GameReview, Integer> {
    Optional<GameReview> findGameReviewByGameTypeAndUser(String gameType, Player player);
    List<GameReview> findAllByUser(Player user);
}
