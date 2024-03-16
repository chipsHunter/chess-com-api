package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
}
