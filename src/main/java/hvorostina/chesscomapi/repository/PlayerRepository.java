package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Optional<Player> findPlayerByUsername(String username);
    Optional<Player> findByPlayerID(Integer playerID);
}
