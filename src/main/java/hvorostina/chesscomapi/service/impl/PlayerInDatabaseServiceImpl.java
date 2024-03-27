package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.in_memory_cache.RequestCache;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.model.mapper.PlayerDTOMapper;
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
    private final RequestCache cache;
    @Override
    public List<PlayerDTO> findAllPlayers() {
        List<Player> players = playerDatabaseRepository.findAll();
        return players.stream()
                .map(playerDTOMapper)
                .toList();
    }

    @Override
    public Optional<PlayerDTO> addPlayer(Player player) {
        Optional<Player> playerInDatabase = playerDatabaseRepository.findPlayerByUsername(player.getUsername());
        if(playerInDatabase.isEmpty())
            return Optional.of(playerDTOMapper
                    .apply(playerDatabaseRepository
                            .save(player)));
        return Optional.empty();
    }

    @Override
    public Optional<PlayerDTO> findPlayerByUsername(String username) {
        String query = "Player " + username;
        Optional<Player> player;
        if(cache.containsQuery(query))
            player = Optional.of((Player) cache.getResponse(query));
        else {
            player = playerDatabaseRepository.findPlayerByUsername(username);
            cache.addQuery(query, player.orElse(null));
        }
        return player.map(playerDTOMapper);
    }
    @Override
    public Optional<PlayerDTO> updatePlayer( PlayerDTO player) {
        Optional<Player> playerInDatabase = playerDatabaseRepository.findPlayerByUsername(player.getUsername());
        if(playerInDatabase.isEmpty())
            return Optional.empty();

        Player updatedPlayer = playerInDatabase.get();
        if(player.getStatus() != null){
            updatedPlayer.setStatus(player.getStatus());
        }
        if(player.getCountry() != null){
            updatedPlayer.setCountry(player.getCountry());
        }
        String query = "Player " + player.getUsername();
        if(cache.containsQuery(query))
            cache.updateResponse(query, updatedPlayer);
        playerDatabaseRepository.save(updatedPlayer);
        return Optional.of(player);
    }
    @Override
    public void deletePlayerByUsername(String username) {
        Optional<Player> playerInDatabase = playerDatabaseRepository.findPlayerByUsername(username);
        if(playerInDatabase.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        String playerQuery = "Player " + username;
        if(cache.containsQuery(playerQuery))
            cache.removeQuery(playerQuery);
        playerDatabaseRepository.delete(playerInDatabase.get());
    }
    @Override
    public int getPlayerIdByUsername(String username) {
        String query = username + " ID";
        if(cache.containsQuery(query))
            return (int)cache.getResponse(query);
        Optional<Player> playerInDatabase = playerDatabaseRepository.findPlayerByUsername(username);
        if(playerInDatabase.isEmpty())
            return 0;
        int id = playerInDatabase.get().getId();
        cache.addQuery(query, id);
        return id;
    }
}
