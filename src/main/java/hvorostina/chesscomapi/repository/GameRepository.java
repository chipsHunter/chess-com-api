package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findGameByUUID(String UUID);
    Optional<Game> findGameByGameURL(String URL);
}
