package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.in_memory_cache.RequestPlayerCacheServiceImpl;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.model.dto.PlayerWithGamesDTO;
import hvorostina.chesscomapi.model.mapper.PlayerDTOMapper;
import hvorostina.chesscomapi.model.mapper.PlayerWithGamesDTOMapper;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Data
@Service
@Transactional
public class PlayerInDatabaseServiceImpl implements PlayerService {
    private final PlayerRepository playerDatabaseRepository;
    private final PlayerDTOMapper playerDTOMapper;
    private final PlayerWithGamesDTOMapper playerWithGamesDTOMapper;
    private final RequestPlayerCacheServiceImpl cache;
    @Override
    public List<PlayerDTO> findAllPlayers() {
        List<Player> players = playerDatabaseRepository.findAll();
        return players.stream()
                .map(playerDTOMapper)
                .toList();
    }
    @Override
    public PlayerDTO addPlayer(Player player) {
        Player addedPlayer = playerDatabaseRepository.save(player);
        PlayerDTO playerDTOForCache = playerDTOMapper.apply(addedPlayer);
        cache.saveOrUpdate(playerDTOForCache);
        return playerDTOForCache;
    }
    @Override
    public PlayerDTO findPlayerByUsernameAndSaveInCache(String username) {
        PlayerDTO playerFromCache = cache.getByUsername(username);
        if(playerFromCache != null)
            return playerFromCache;
        Optional<Player> playerFromDatabase = playerDatabaseRepository.findPlayerByUsername(username);
        PlayerDTO playerToCache =  playerDTOMapper.apply(playerFromDatabase.orElseThrow(() ->
                new HttpClientErrorException(HttpStatus.NOT_FOUND))
        );
        cache.saveOrUpdate(playerToCache);
        return playerToCache;
    }
    @Override
    public List<PlayerWithGamesDTO> getAllPlayersWithGames() {
        List<Player> players = playerDatabaseRepository.findAll();
        return players.stream()
                .map(playerWithGamesDTOMapper)
                .toList();
    }
    @Override
    public Player findPlayerEntityByUsername(String username) {
        Optional<Player> player = playerDatabaseRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        return player.get();
    }

    @Override
    public PlayerDTO updatePlayerAndSaveInCache(PlayerDTO fields) {
        Optional<Player> playerFromDatabase = playerDatabaseRepository.findPlayerByUsername(fields.getUsername());
        if(playerFromDatabase.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Player actualPlayer = playerFromDatabase.get();
        updatePlayerFields(actualPlayer, fields);
        playerDatabaseRepository.save(actualPlayer);
        PlayerDTO playerDTOToCache = playerDTOMapper.apply(actualPlayer);
        cache.saveOrUpdate(playerDTOToCache);
        return playerDTOToCache;
    }
    private void updatePlayerFields(Player player, PlayerDTO fields) {
        if(fields.getStatus() != null){
            player.setStatus(fields.getStatus());
        }
        if(fields.getCountry() != null){
            player.setCountry(fields.getCountry());
        }
    }
    @Override
    public void deletePlayerByUsername(String username) {
        cache.delete(username);
        Optional<Player> playerInDatabase = playerDatabaseRepository.findPlayerByUsername(username);
        if(playerInDatabase.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        playerDatabaseRepository.delete(playerInDatabase.get());
    }
    @Override
    public Integer getIdByUsername(String username) {
        Optional<Player> playerInDatabase = playerDatabaseRepository.findPlayerByUsername(username);
        if(playerInDatabase.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        return playerInDatabase.get().getId();
    }
}
