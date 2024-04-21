package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface GameService {
    Game addGame(Game game);
    Game updateGameResult(GameDTO fields);
    List<Game> findAllGamesByUsername(String username);
    List<Game> findGamesByUserBetweenDates(
            Integer id, LocalDateTime start, LocalDateTime end);
    Game findGameByUUID(String uuid);
    void deleteGame(String uuid);
    void deleteAllGames();
}
