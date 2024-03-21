package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.dto.GameDTO;

import java.util.List;
import java.util.Optional;

public interface GameService {
    Optional<GameDTO> addGame(GameDTO game);
    Optional<GameDTO> updateGameResult(GameDTO gameParams);
    List<GameDTO> findAllGames();
    Optional<GameDTO> findGameByUUID(String uuid);
    void deleteGame(String uuid);
    void deleteAllGames();
}
