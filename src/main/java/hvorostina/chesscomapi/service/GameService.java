package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Optional<GameDTOWithZonedTimeDate> addGame(GameDTO game);
    Optional<GameDTOWithZonedTimeDate> updateGameResult(GameDTO gameParams);
    List<GameDTOWithZonedTimeDate> findAllGamesByUsername(String username);
    Optional<GameDTOWithZonedTimeDate> findGameByUUID(String uuid);
    void deleteGame(String uuid);
    void deleteAllGames();
}
