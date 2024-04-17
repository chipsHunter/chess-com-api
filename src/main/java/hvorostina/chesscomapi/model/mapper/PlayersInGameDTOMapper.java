package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
@Component
public final class PlayersInGameDTOMapper implements
        Function<Game, List<PlayerInGameDTO>> {
    @Override
    public List<PlayerInGameDTO> apply(final Game game) {
        if (game.getPlayers() == null || game.getPlayers().isEmpty()) {
            return List.of();
        }
        List<PlayerInGameDTO> playersInGameDTO = new ArrayList<>();
        PlayerInGameDTO whitePlayer = PlayerInGameDTO.builder()
                .username(game.getPlayers().get(0).getUsername())
                .rating(game.getWhiteRating())
                .build();
        PlayerInGameDTO blackPlayer = PlayerInGameDTO.builder()
                .username(game.getPlayers().get(1).getUsername())
                .rating(game.getBlackRating())
                .build();
        if (Objects.equals(game.getWinnerSide(), "white")) {
            whitePlayer.setGameResult("win");
            blackPlayer.setGameResult(game.getGameResult());
        } else {
            blackPlayer.setGameResult("win");
            whitePlayer.setGameResult(game.getGameResult());
        }
        playersInGameDTO.add(0, whitePlayer);
        playersInGameDTO.add(1, blackPlayer);
        return playersInGameDTO;
    }
}
