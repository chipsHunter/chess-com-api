package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;
import hvorostina.chesscomapi.model.dto.UserGamesInPeriodRequestDTO;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Optional<GameDTOWithZonedTimeDate> addGame(GameDTO game);
    Optional<GameDTOWithZonedTimeDate> updateGameResult(GameDTO gameParams);
    List<GameDTOWithZonedTimeDate> findAllGamesByUsername(String username);
    List<GameDTOWithZonedTimeDate> findGamesByUserBetweenDates(UserGamesInPeriodRequestDTO requestDTO);
    Optional<GameDTOWithZonedTimeDate> findGameByUUID(String uuid);
    void deleteGame(String uuid);
    void deleteAllGames();
}
