package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.GameReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameReviewRepository extends JpaRepository<GameReview, Integer> {
}
