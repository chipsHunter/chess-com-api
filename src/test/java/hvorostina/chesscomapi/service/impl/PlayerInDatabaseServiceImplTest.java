package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.in_memory_cache.RequestPlayerCacheServiceImpl;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PlayerInDatabaseServiceImplTest {
    @Mock
    PlayerRepository repository;
    @Mock
    RequestPlayerCacheServiceImpl cache;
    @InjectMocks
    PlayerInDatabaseServiceImpl service;
    Player player;
    String username;

    @BeforeEach
    void initializePlayer() {
        username = "test";
        player = Player.builder()
                .id(1)
                .username(username)
                .country("BY")
                .status("basic")
                .build();
    }

    @Test
    void savePlayerList() {
        List<Player> players = List.of(player);

        when(repository.saveAll(players)).thenReturn(players);

        service.bulkInsertPlayers(players);

        verify(repository, times(1)).saveAll(players);
    }
    @Test
    void findAllPlayers_noPlayers_thenReturnEmptyList() {
        List<Player> expectedPlayers = List.of();
        when(repository.findAll()).thenReturn(expectedPlayers);

        List<Player> reallyFoundPlayers = service.findAllPlayers();

        assertEquals(0, reallyFoundPlayers.size());
    }

    @Test
    void findAllPlayers_hasPlayers_thenReturnPlayerList() {
        List<Player> expectedPlayers = List.of(player);
        when(repository.findAll()).thenReturn(expectedPlayers);

        List<Player> reallyFoundPlayers = service.findAllPlayers();

        assertEquals(1, reallyFoundPlayers.size());
    }
    @Test
    void addPlayer_thenReturnPlayer() {
        doNothing().when(cache).saveOrUpdate(player);

        service.addPlayer(player);

        verify(repository, times(1)).save(player);
    }

    @Test
    void findByUsername_existInCache_thenReturnFromCache() {
        when(cache.getByUsername(username)).thenReturn(player);

        service.findPlayerByUsernameAndSaveInCache(username);

        verify(cache, times(0)).saveOrUpdate(player);
    }

    @Test
    void findByUsername_existInDatabase_thenSaveInCacheAndReturnFromDatabase() {
        when(cache.getByUsername(username)).thenReturn(null);
        when(repository.findPlayerByUsername(username)).thenReturn(Optional.of(player));

        service.findPlayerByUsernameAndSaveInCache(username);

        verify(cache, times(1)).saveOrUpdate(player);
    }
    @Test
    void findByUsername_notExistInDatabase_thenThrowNotFound() {
        when(cache.getByUsername(username)).thenReturn(null);
        when(repository.findPlayerByUsername(username)).thenReturn(Optional.empty());

        assertThrows(HttpClientErrorException.class, () ->
                service.findPlayerByUsernameAndSaveInCache(username));
    }
    @Test
    void updatePlayer_noFieldsSpecifiedAndPlayerExist_thenDoNothing() {
        PlayerDTO fields = PlayerDTO.builder()
                .username(username)
                .build();
        when(repository.findPlayerByUsername(username)).thenReturn(Optional.of(player));
        when(repository.save(player)).thenReturn(player);

        Player reallyUpdatedPlayer = service.updatePlayerAndSaveInCache(fields);

        assertEquals(player.getCountry(), reallyUpdatedPlayer.getCountry());
        assertEquals(player.getStatus(), reallyUpdatedPlayer.getStatus());
    }

    @Test
    void updatePlayer_fieldsSpecifiedAndPlayerExist_thenUpdate() {
        PlayerDTO fields = PlayerDTO.builder()
                .username(username)
                .status("silver")
                .country("RU")
                .build();
        when(repository.findPlayerByUsername(username)).thenReturn(Optional.of(player));

        Player reallyUpdatedPlayer = service.updatePlayerAndSaveInCache(fields);

        assertEquals(fields.getCountry(), reallyUpdatedPlayer.getCountry());
        assertEquals(fields.getStatus(), reallyUpdatedPlayer.getStatus());
    }

    @Test
    void deletePlayer() {
        service.deletePlayer(player);

        verify(repository, times(1)).delete(player);
    }

    @Test
    void getPlayerIdByUsername_playerNotExist_thenThrowNotFound() {
        when(repository.findPlayerByUsername(username)).thenReturn(Optional.of(player));

        int id = service.getIdByUsername(username);

        assertEquals(player.getId(), id);
    }
}