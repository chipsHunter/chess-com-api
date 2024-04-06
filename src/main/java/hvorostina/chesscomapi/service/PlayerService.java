package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.model.dto.PlayerWithGamesDTO;

import java.util.List;

public interface PlayerService {
    List<PlayerDTO> findAllPlayers();
    PlayerDTO addPlayer(Player player);
    PlayerDTO findPlayerByUsernameAndSaveInCache(String username);
    Player findPlayerEntityByUsername(String username);
    PlayerDTO updatePlayerAndSaveInCache(PlayerDTO fields);
    void deletePlayer(Player player);
    Integer getIdByUsername(String username);

    List<PlayerWithGamesDTO> getAllPlayersWithGames();
}

