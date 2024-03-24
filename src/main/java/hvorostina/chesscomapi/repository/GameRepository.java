package hvorostina.chesscomapi.repository;

import hvorostina.chesscomapi.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Integer> {
    Optional<Game> findGameByUuid(String uuid);
    @Query(
        value = "with find_user as (" +
                    "select game_id from chess_matches cm " +
                        "where cm.player_id = :id" +
                ")" +
                "select gm.* from game gm inner join find_user f " +
                    "on gm.gameid = f.game_id where gm.data > :start and " +
                        "gm.data < :end ;",
        nativeQuery = true)
    List<Game> findGamesByPlayerInPeriod(@Param("id") int playerID, @Param("start") Long startTimestamp, @Param("end") Long endTimestamp);
}
