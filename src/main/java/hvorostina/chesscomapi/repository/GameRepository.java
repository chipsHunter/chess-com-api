package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findGameByUuid(String uuid);
    Optional<Game> findGameByGameURL(String url);
    List<Game> findAllByPlayers(List<Player> players);
}
