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
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Data
@Service
@Transactional
public class GameInDatabaseServiceImpl implements GameService {
    private final GameDTOWithDateMapper gameDTOWithDateMapper;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameReviewRepository gameReviewRepository;
    private final RequestGamesCacheServiceImpl cacheService;
    @Override
    public GameDTOWithDate addGame(Game game) {
        if (gameRepository.findGameByUuid(game.getUuid()).isEmpty())
            gameRepository.save(game);
        return gameDTOWithDateMapper.apply(game);
    }
    @Override
    @AspectAnnotation
    public Game getByUuid(String uuid) {
        Optional<Game> game = gameRepository.findGameByUuid(uuid);
        return game.orElseThrow(() ->
                new HttpClientErrorException(HttpStatus.NOT_FOUND));
    }
    @Override
    @AspectAnnotation
    public GameDTOWithDate updateGameResult(GameDTO fields) {
        Optional<Game> updatedGame = gameRepository.findGameByUuid(fields.getUuid());
        if (updatedGame.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        setFieldsIfSpecified(updatedGame.get(), fields);
        gameRepository.save(updatedGame.get());
        GameDTOWithDate gameDTOWithDate = gameDTOWithDateMapper.apply(updatedGame.get());
        cacheService.saveOrUpdateByUuid(gameDTOWithDate);
        return gameDTOWithDate;
    }
    private void setFieldsIfSpecified(Game updatedGame, GameDTO fields) {
        setDataIfSpecified(updatedGame, fields);
        setURLIfSpecified(updatedGame, fields);
    }
    private void setDataIfSpecified(Game updatedGame, GameDTO fields) {
        if (fields.getGameTimestamp() != null) {
            Instant instant = Instant.ofEpochSecond(fields.getGameTimestamp());
            LocalDateTime data = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Minsk"));
            updatedGame.setData(data);
        }
    }
    private void setURLIfSpecified(Game updatedGame, GameDTO fields) {
        if (fields.getGameURL() != null)
            updatedGame.setGameURL(fields.getGameURL());
    }

    @Override
    @AspectAnnotation
    public List<GameDTOWithDate> findAllGamesByUsername(String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        List<Game> playerGames = player.get().getGames();
        List<GameDTOWithDate> playerGameDTOsForCache = playerGames.stream()
                .map(gameDTOWithDateMapper)
                .toList();
        cacheService.saveByUser(username, playerGameDTOsForCache);
        return playerGameDTOsForCache;
    }

    @Override
    @AspectAnnotation
    public List<GameDTOWithDate> findGamesByUserBetweenDates(Integer id, LocalDateTime start, LocalDateTime end) {
        List<Game> games = gameRepository.findGamesByPlayerInPeriod(id, start, end);
        return games.stream().map(gameDTOWithDateMapper).toList();
    }

    @Override
    @AspectAnnotation
    public GameDTOWithDate findGameByUUID(String uuid) {
        GameDTOWithDate gameInCache = cacheService.getByUuid(uuid);
        if(gameInCache != null)
            return gameInCache;
        Optional<Game> game = gameRepository.findGameByUuid(uuid);
        if(game.isEmpty())
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
        GameDTOWithDate gameDTO = gameDTOWithDateMapper.apply(game.get());
        cacheService.saveOrUpdateByUuid(gameDTO);
        return gameDTO;
    }

    @Override
    @AspectAnnotation
    public void deleteAllGames() {
        cacheService.deleteAll();
        gameRepository.deleteAll();
    }

    @Override
    @AspectAnnotation
    public void deleteGame(String uuid) {
        Optional<Game> game = gameRepository.findGameByUuid(uuid);
        if(game.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        cacheService.deleteByUuid(uuid);
        gameRepository.delete(game.get());
    }
}
