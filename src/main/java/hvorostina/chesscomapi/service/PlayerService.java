package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;

import java.util.List;
import java.util.Optional;

public interface PlayerService {
    List<PlayerDTO> findAllPlayers();
    Optional<PlayerDTO> addPlayer(Player player);
    Optional<PlayerDTO> findPlayerByUsername(String username);
    public Optional<PlayerDTO> updatePlayer(PlayerDTO player);
    void deletePlayerByUsername(String username);
}
