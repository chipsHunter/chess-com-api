package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTO;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Optional;

public interface GameService {
    Optional<GameDTO> addGame(GameDTO game);
    Optional<GameDTO> updateGameResult(GameDTO gameParams);
    List<GameDTO> findAllGames();
    Optional<GameDTO> findGameByUUID(String UUID);
    void deleteGame(String UUID);
}
