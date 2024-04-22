package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;

import java.util.List;

public interface PlayerService {
    List<Player> findAllPlayers();
    Player addPlayer(Player player);
    Player findPlayerByUsernameAndSaveInCache(String username);
    Player findPlayerEntityByUsername(String username);
    Player updatePlayerAndSaveInCache(PlayerDTO fields);
    void deletePlayer(Player player);
    Integer getIdByUsername(String username);
}

