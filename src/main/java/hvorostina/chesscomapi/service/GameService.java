package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;

import java.time.LocalDateTime;
import java.util.List;

public interface GameService {
    GameDTOWithZonedTimeDate addGame(Game game);
    GameDTOWithZonedTimeDate updateGameResult(GameDTO fields);
    List<GameDTOWithZonedTimeDate> findAllGamesByUsername(String username);
    List<GameDTOWithZonedTimeDate> findGamesByUserBetweenDates(Integer id, LocalDateTime start, LocalDateTime end);
    GameDTOWithZonedTimeDate findGameByUUID(String uuid);
    void deleteGame(String uuid);
    void deleteAllGames();
}
