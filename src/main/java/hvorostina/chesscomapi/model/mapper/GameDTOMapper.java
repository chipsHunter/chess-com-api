package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.GameDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.function.Function;

@AllArgsConstructor
@Component
public class GameDTOMapper implements Function<Game, GameDTO> {
    private final PlayersInGameDTOMapper gameDTOMapper;
    @Override
    public GameDTO apply(Game game) {
        if(game.getPlayers().isEmpty())
            return null;
        return GameDTO.builder()
                .gameURL(game.getGameURL())
                .gameTimestamp(game.getData())
                .gameData(LocalDateTime.ofInstant(game.getData().toInstant(), ZoneId.of("UTC+03:00")))
                .UUID(game.getUUID())
                .timeClass(game.getTimeClass())
                .whitePlayer(gameDTOMapper.apply(game).get(0))
                .blackPlayer(gameDTOMapper.apply(game).get(1))
                .build();
    }
}
