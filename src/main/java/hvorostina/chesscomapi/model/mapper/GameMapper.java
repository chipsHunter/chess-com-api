package hvorostina.chesscomapi.model.mapper;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import hvorostina.chesscomapi.repository.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class GameMapper implements Function<GameDTO, Game> {
    private final PlayerRepository playerRepository;
    @Override
    public Game apply(final GameDTO gameDTO) {

        Instant instant = Instant.ofEpochSecond(gameDTO.getGameTimestamp());
        LocalDateTime data = LocalDateTime
                .ofInstant(instant, ZoneId.of("Europe/Minsk"));

        List<Player> players = getPlayerList(gameDTO);
        String winnerSide = getWinnerSide(gameDTO);
        String gameResult = getGameResult(gameDTO);

        return Game.builder()
                .gameURL(gameDTO.getGameURL())
                .timeClass(gameDTO.getTimeClass())
                .data(data)
                .uuid(gameDTO.getUuid())
                .players(players)
                .gameResult(gameResult)
                .winnerSide(winnerSide)
                .whiteRating(gameDTO.getWhitePlayer().getRating())
                .blackRating(gameDTO.getBlackPlayer().getRating())
                .build();
    }
    private List<Player> getPlayerList(final GameDTO game) {
        List<Player> players = new ArrayList<>();
        Player whitePlayer = getPlayerFromGame(game.getWhitePlayer());
        players.add(0, whitePlayer);
        Player blackPlayer = getPlayerFromGame(game.getBlackPlayer());
        players.add(1, blackPlayer);
        return players;
    }
    private Player getPlayerFromGame(final PlayerInGameDTO side) {
        String lowercaseUsername = side.getUsername().toLowerCase();
        Optional<Player> player = playerRepository
                .findPlayerByUsername(lowercaseUsername);
        return player.orElseThrow(() ->
                new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }
    private String getWinnerSide(final GameDTO game) {
        PlayerInGameDTO whitePlayer = game.getWhitePlayer();
        PlayerInGameDTO blackPlayer = game.getBlackPlayer();
        String whitePlayerResult = whitePlayer.getGameResult();
        String blackPlayerResult = blackPlayer.getGameResult();
        if (whitePlayerResult.equals(blackPlayerResult)) {
            return null;
        }
        if (whitePlayerResult.equals("win")) {
            return "white";
        }
        return "black";
    }
    private String getGameResult(final GameDTO game) {
        PlayerInGameDTO whitePlayer = game.getWhitePlayer();
        PlayerInGameDTO blackPlayer = game.getBlackPlayer();
        String whitePlayerResult = whitePlayer.getGameResult();
        String blackPlayerResult = blackPlayer.getGameResult();
        if (whitePlayerResult.equals(blackPlayerResult)) {
            return "draw";
        }
        if (whitePlayerResult.equals("win")) {
            return blackPlayerResult;
        }
        return whitePlayerResult;
    }
}
