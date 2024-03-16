package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.dto.PlayerDTO;

import java.util.List;
import java.util.Optional;

public interface PlayerService {
    List<PlayerDTO> findAllPlayers();
    PlayerDTO addPlayer(PlayerDTO playerDTO);
    Optional<PlayerDTO> findPlayerByUsername(String username);
    Optional<PlayerDTO> updatePlayer(PlayerDTO playerDTO);
    void deletePlayerByUsername(String username);
}
