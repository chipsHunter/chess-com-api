package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;

import java.time.LocalDateTime;
import java.util.List;

public interface GameService {
    GameDTOWithDate addGame(Game game);
    GameDTOWithDate updateGameResult(GameDTO fields);
    List<GameDTOWithDate> findAllGamesByUsername(String username);
    List<GameDTOWithDate> findGamesByUserBetweenDates(Integer id, LocalDateTime start, LocalDateTime end);
    GameDTOWithDate findGameByUUID(String uuid);
    Game getByUuid(String uuid);
    void deleteGame(String uuid);
    void deleteAllGames();
}
