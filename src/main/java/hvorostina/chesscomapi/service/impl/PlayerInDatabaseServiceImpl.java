package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.annotations.AspectAnnotation;
import hvorostina.chesscomapi.in_memory_cache.RequestPlayerCacheServiceImpl;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
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
    private final RequestPlayerCacheServiceImpl cache;
    @Override
    @AspectAnnotation
    public List<Player> findAllPlayers() {
        return playerDatabaseRepository.findAll();
    }
    @Override
    @AspectAnnotation
    public List<Player> bulkInsertPlayers(List<Player> players) {
        return playerDatabaseRepository.saveAll(players);
    }
    @Override
    @AspectAnnotation
    public Player addPlayer(final Player player) {
        cache.saveOrUpdate(player);
        return playerDatabaseRepository.save(player);
    }
    @Override
    @AspectAnnotation
    public Player findPlayerByUsernameAndSaveInCache(
            final String username) {
        Player playerFromCache = cache.getByUsername(username);
        if (playerFromCache != null) {
            return playerFromCache;
        }
        Player player = findPlayerEntityByUsername(username);
        cache.saveOrUpdate(player);
        return player;
    }
    @Override
    public Player findPlayerEntityByUsername(final String username) {
        Optional<Player> player = playerDatabaseRepository
                .findPlayerByUsername(username);
        if (player.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        return player.get();
    }

    @Override
    @AspectAnnotation
    public Player updatePlayerAndSaveInCache(final PlayerDTO fields) {
        Player actualPlayer = findPlayerEntityByUsername(fields.getUsername());
        updatePlayerFields(actualPlayer, fields);
        cache.saveOrUpdate(actualPlayer);
        playerDatabaseRepository.save(actualPlayer);
        return actualPlayer;
    }
    private void updatePlayerFields(
            final Player player, final PlayerDTO fields) {
        if (fields.getStatus() != null) {
            player.setStatus(fields.getStatus());
        }
        if (fields.getCountry() != null) {
            player.setCountry(fields.getCountry());
        }
    }
    @Override
    @AspectAnnotation
    public void deletePlayer(final Player player) {
        cache.delete(player.getUsername());
        playerDatabaseRepository.delete(player);
    }
    @Override
    public Integer getIdByUsername(final String username) {
        Player playerInDatabase =
                findPlayerEntityByUsername(username);
        return playerInDatabase.getId();
    }
}
