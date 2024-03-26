package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findGameByUuid(String uuid);
    @Query(
        value = "select gm.* from game gm inner join chess_matches cm on cm.game_id=gm.id " +
                "inner join player pl on cm.player_id = pl.id " +
                "where cm.player_id = :id and gm.data > :start and gm.data < :end ",
        nativeQuery = true)
    List<Game> findGamesByPlayerInPeriod(@Param("id") int playerID, @Param("start") LocalDateTime startTimestamp, @Param("end") LocalDateTime endTimestamp);
}
