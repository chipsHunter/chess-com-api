package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.in_memory_cache.RequestCache;
import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;
import hvorostina.chesscomapi.model.mapper.GameDTOWithZoneTimeDateMapper;
import hvorostina.chesscomapi.repository.GameRepository;
import hvorostina.chesscomapi.repository.GameReviewRepository;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@lombok.Data
@Service
@Transactional
public class GameInDatabaseServiceImpl implements GameService {
    private final GameDTOWithZoneTimeDateMapper gameDTOWithZoneTimeDateMapper;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameReviewRepository gameReviewRepository;
    private final RequestCache cache;
    @Override
    public Optional<GameDTOWithZonedTimeDate> addGame(GameDTO game) {
        if (gameRepository.findGameByUuid(game.getUuid()).isPresent())
            return Optional.empty();
        List<Player> players = getPlayersOrEmptyList(game);
        if (players.isEmpty())
            return Optional.empty();
        Game newGame = new Game();
        newGame.setGameURL(game.getGameURL().toString());
        Instant instant = Instant.ofEpochSecond(game.getGameTimestamp());
        LocalDateTime data = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Minsk"));
        newGame.setData(data);
        newGame.setUuid(game.getUuid());
        newGame.setPlayers(players);
        newGame.setWhiteRating(game.getWhitePlayer().getRating());
        newGame.setBlackRating(game.getBlackPlayer().getRating());
        if (Objects.equals(game.getWhitePlayer().getGameResult(), "win")) {
            newGame.setWinnerSide("white");
            newGame.setGameResult(game.getBlackPlayer().getGameResult());
        } else {
            newGame.setWinnerSide("black");
            newGame.setGameResult(game.getWhitePlayer().getGameResult());
        }
        newGame.setTimeClass(game.getTimeClass());
        return Optional.of(gameDTOWithZoneTimeDateMapper.apply(gameRepository.save(newGame)));
    }
    public List<Player> getPlayersOrEmptyList(GameDTO game) {
        List<Player> players = new ArrayList<>();
        Optional<Player> whitePlayer = playerRepository
                .findPlayerByUsername(game
                        .getWhitePlayer()
                        .getUsername().toLowerCase());
        if (whitePlayer.isEmpty()) {
            return List.of();
        }
        players.add(0, whitePlayer.get());
        Optional<Player> blackPlayer = playerRepository
                .findPlayerByUsername(game
                        .getBlackPlayer()
                        .getUsername().toLowerCase());
        if (blackPlayer.isEmpty()) {
            return List.of();
        }
        players.add(1, blackPlayer.get());
        return players;
    }

    @Override
    public Optional<GameDTOWithZonedTimeDate> updateGameResult(GameDTO gameParams) {
        Optional<Game> updatedGame = gameRepository.findGameByUuid(gameParams.getUuid());
        if (updatedGame.isEmpty())
            return Optional.empty();
        if (gameParams.getGameTimestamp() != null) {
            Instant instant = Instant.ofEpochSecond(gameParams.getGameTimestamp());
            LocalDateTime data = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Minsk"));
            updatedGame.get().setData(data);
        }
        if (gameParams.getGameURL() != null)
            updatedGame.get().setGameURL(gameParams.getGameURL().toString());
        return Optional.of(gameDTOWithZoneTimeDateMapper.apply(gameRepository.save(updatedGame.get())));
    }

    @Override
    public List<GameDTOWithZonedTimeDate> findAllGamesByUsername(String username) {
        return gameRepository.findAll()
                .stream()
                .filter(game -> game.getPlayers().get(0).getUsername().equals(username) ||
                        game.getPlayers().get(1).getUsername().equals(username))
                .map(gameDTOWithZoneTimeDateMapper)
                .toList();
    }

    @Override
    public List<GameDTOWithZonedTimeDate> findGamesByUserBetweenDates(Integer id, LocalDateTime start, LocalDateTime end) {
        List<Game> games = gameRepository.findGamesByPlayerInPeriod(id, start, end);
        return games.stream().map(gameDTOWithZoneTimeDateMapper).toList();
    }

    @Override
    public Optional<GameDTOWithZonedTimeDate> findGameByUUID(String uuid) {
        return gameRepository
                .findGameByUuid(uuid)
                .map(gameDTOWithZoneTimeDateMapper);
    }

    @Override
    public void deleteAllGames() {
        gameRepository.deleteAll();
    }

    @Override
    public void deleteGame(String uuid) {
        Optional<Game> game = gameRepository.findGameByUuid(uuid);
        if(game.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        gameRepository.delete(game.get());
    }
}
