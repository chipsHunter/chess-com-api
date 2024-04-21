package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;

import java.time.LocalDateTime;
import java.util.List;

public interface GameService {
    GameDTOWithDate addGame(Game game);
    Game updateGameResult(GameDTO fields);
    List<Game> findAllGamesByUsername(String username);
    List<Game> findGamesByUserBetweenDates(
            Integer id, LocalDateTime start, LocalDateTime end);
    Game findGameByUUID(String uuid);
    void deleteGame(String uuid);
    void deleteAllGames();
}
