package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public final class PlayerMapper implements Function<PlayerDTO, Player> {
    @Override
    public Player apply(final PlayerDTO playerDTO) {
        return Player.builder()
                .id(playerDTO.getId())
                .country(playerDTO.getCountry())
                .username(playerDTO.getUsername())
                .status(playerDTO.getStatus())
                .build();
    }
}
