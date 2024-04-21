package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.annotations.AspectAnnotation;
import hvorostina.chesscomapi.in_memory_cache.RequestGamesCacheServiceImpl;
import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import hvorostina.chesscomapi.model.mapper.GameDTOWithDateMapper;
import hvorostina.chesscomapi.repository.GameRepository;
import hvorostina.chesscomapi.repository.GameReviewRepository;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.GameService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@AllArgsConstructor
@Service
@Transactional
public class GameInDatabaseServiceImpl implements GameService {
    private final GameDTOWithDateMapper gameDTOWithDateMapper;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameReviewRepository gameReviewRepository;
    private final RequestGamesCacheServiceImpl cacheService;
    @Override
    public GameDTOWithDate addGame(final Game game) {
        if (gameRepository.findGameByUuid(game.getUuid()).isEmpty()) {
            gameRepository.save(game);
        }
        return gameDTOWithDateMapper.apply(game);
    }
    @Override
    @AspectAnnotation
    public Game updateGameResult(final GameDTO fields) {
        Optional<Game> updatedGame = gameRepository
                .findGameByUuid(fields.getUuid());
        if (updatedGame.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        setFieldsIfSpecified(updatedGame.get(), fields);
        gameRepository.save(updatedGame.get());
        cacheService.saveOrUpdateByUuid(updatedGame.get());
        return updatedGame.get();
    }
    public void setFieldsIfSpecified(
            final Game updatedGame, final GameDTO fields) {
        setDataIfSpecified(updatedGame, fields);
        setURLIfSpecified(updatedGame, fields);
    }
    private void setDataIfSpecified(
            final Game updatedGame, final GameDTO fields) {
        if (fields.getGameTimestamp() != null) {
            Instant instant = Instant.ofEpochSecond(fields.getGameTimestamp());
            LocalDateTime data = LocalDateTime
                    .ofInstant(instant, ZoneId.of("Europe/Minsk"));
            updatedGame.setData(data);
        }
    }
    private void setURLIfSpecified(
            final Game updatedGame, final GameDTO fields) {
        if (fields.getGameURL() != null) {
            updatedGame.setGameURL(fields.getGameURL());
        }
    }

    @Override
    @AspectAnnotation
    public List<Game> findAllGamesByUsername(
            final String username) {
        Optional<Player> player = playerRepository
                .findPlayerByUsername(username);
        if (player.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        List<Game> playerGames = player.get().getGames();
        cacheService.saveByUser(username, playerGames);
        return playerGames;
    }

    @Override
    @AspectAnnotation
    public List<Game> findGamesByUserBetweenDates(
            final Integer id,
            final LocalDateTime start,
            final LocalDateTime end) {
        return gameRepository
                .findGamesByPlayerInPeriod(id, start, end);
    }

    @Override
    @AspectAnnotation
    public Game findGameByUUID(final String uuid) {
        Game gameInCache = cacheService.getByUuid(uuid);
        if (gameInCache != null) {
            return gameInCache;
        }
        Optional<Game> game = gameRepository.findGameByUuid(uuid);
        if (game.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        cacheService.saveOrUpdateByUuid(game.get());
        return game.get();
    }

    @Override
    @AspectAnnotation
    public void deleteAllGames() {
        cacheService.deleteAll();
        gameRepository.deleteAll();
    }

    @Override
    @AspectAnnotation
    public void deleteGame(final String uuid) {
        Optional<Game> game = gameRepository.findGameByUuid(uuid);
        if (game.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        cacheService.deleteByUuid(uuid);
        gameRepository.delete(game.get());
    }
}
