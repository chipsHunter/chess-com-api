package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.model.mapper.PlayerDTOMapper;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerDatabaseRepository;
    private final PlayerDTOMapper playerDTOMapper;
    @Override
    public List<PlayerDTO> findAllPlayers() {
        List<Player> players = playerDatabaseRepository.findAll();
        return players.stream()
                .map(playerDTOMapper)
                .collect(Collectors.toList());
    }

    @Override
    public PlayerDTO addPlayer(PlayerDTO playerDTO) {
        return null;
    }

    @Override
    public Optional<PlayerDTO> findPlayerByUsername(String username) {
        return Optional.empty();
    }

    @Override
    public Optional<PlayerDTO> updatePlayer(PlayerDTO playerDTO) {
        return Optional.empty();
    }

    @Override
    public void deletePlayerByUsername(String username) {

    }
}
