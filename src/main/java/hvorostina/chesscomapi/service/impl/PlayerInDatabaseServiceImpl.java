package hvorostina.chesscomapi.service.impl;

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
import java.util.stream.Collectors;

@Data
@Service
@Transactional
public class PlayerInDatabaseServiceImpl implements PlayerService {
    private final PlayerRepository playerDatabaseRepository;
    private final PlayerDTOMapper playerDTOMapper;
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
        Optional<Player> player = playerDatabaseRepository.findPlayerByUsername(username);
        return player.map(playerDTOMapper);
    }
    @Override
    public Optional<PlayerDTO> updatePlayer(PlayerDTO player) {
        Optional<Player> playerInDatabase = playerDatabaseRepository.findByPlayerID(player.getPlayerID());
        if(playerInDatabase.isEmpty())
            return Optional.empty();

        Player updatedPlayer = playerInDatabase.get();
        updatedPlayer.setStatus(player.getStatus());
        updatedPlayer.setCountry(player.getCountry());
        updatedPlayer.setUsername(player.getUsername());
        playerDatabaseRepository.save(updatedPlayer);

        return Optional.of(player);
    }
    @Override
    public void deletePlayerByUsername(String username) {
        Optional<Player> playerInDatabase = playerDatabaseRepository.findPlayerByUsername(username);
        if(playerInDatabase.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        playerDatabaseRepository.delete(playerInDatabase.get());
    }
}
