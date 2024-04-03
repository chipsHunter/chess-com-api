package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;

import java.util.List;

public interface PlayerService {
    List<PlayerDTO> findAllPlayers();
    PlayerDTO addPlayer(Player player);
    PlayerDTO findPlayerByUsernameAndSaveInCache(String username);
    Player findPlayerEntityByUsername(String username);
    PlayerDTO updatePlayerAndSaveInCache(PlayerDTO fields);
    void deletePlayerByUsername(String username);
    Integer getIdByUsername(String username);
}

