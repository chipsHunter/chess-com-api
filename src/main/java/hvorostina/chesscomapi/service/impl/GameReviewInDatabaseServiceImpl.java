package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.in_memory_cache.RequestCache;
import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import hvorostina.chesscomapi.model.mapper.GameReviewDTOMapper;
import hvorostina.chesscomapi.repository.GameRepository;
import hvorostina.chesscomapi.repository.GameReviewRepository;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.GameReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class GameReviewInDatabaseServiceImpl implements GameReviewService {
    private final GameReviewRepository gameReviewRepository;
    private final GameReviewDTOMapper gameReviewDTOMapper;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final RequestCache cache;
    private static final String CHECKMATED = "checkmated";
    private static final String WIN = "win";
    private static final String LOSS = "loss";
    private static final String DRAW = "draw";
    private static final String WHITE = "white";
    private static final String BLACK = "black";
    private static final int ADD = 1;
    private static final int DELETE = -1;
    @Override
    public void manageGameReviewForNewGame(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate) {
        Optional<Player> whitePlayer = playerRepository.findPlayerByUsername(gameDTOWithZonedTimeDate.getWhitePlayer().getUsername());
        if(whitePlayer.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<Player> blackPlayer = playerRepository.findPlayerByUsername(gameDTOWithZonedTimeDate.getBlackPlayer().getUsername());
        if(blackPlayer.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<GameReview> existingGameReview = gameReviewRepository
                .findAllByUser(whitePlayer.get()).stream()
                .filter(gameReview -> gameReview.getBestGame().getTimeClass().equals(gameDTOWithZonedTimeDate.getTimeClass()))
                .findAny();
        if(existingGameReview.isEmpty()) {
            createTimeClassReview(gameDTOWithZonedTimeDate, whitePlayer.get());
            createTimeClassReview(gameDTOWithZonedTimeDate, blackPlayer.get());
        } else {
            updateTimeClassReviewByAddingGame(gameDTOWithZonedTimeDate, whitePlayer.get());
            updateTimeClassReviewByAddingGame(gameDTOWithZonedTimeDate, blackPlayer.get());
        }
    }
    @Override
    public void createTimeClassReview(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate, Player player) {
        Optional<Game> game = gameRepository.findGameByUuid(gameDTOWithZonedTimeDate.getUuid());
        if(game.isEmpty())
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        PlayerInGameDTO playerResults;
        if(gameDTOWithZonedTimeDate.getWhitePlayer().getUsername().equals(player.getUsername()))
            playerResults = gameDTOWithZonedTimeDate.getWhitePlayer();
        else
            playerResults = gameDTOWithZonedTimeDate.getBlackPlayer();
        GameReview gameReview = new GameReview();
        gameReview.setBestGame(game.get());
        gameReview.setUser(player);
        changePlayerReviewRecords(gameReview, playerResults, ADD);
        gameReviewRepository.save(gameReview);
    }
    @Override
    public void deleteAllReviewsByUsername(String username) {
        Optional<Player> playerInDatabase = playerRepository.findPlayerByUsername(username);
        if(playerInDatabase.isEmpty())
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        gameReviewRepository.deleteAllByUser(playerInDatabase.get());
    }
    @Override
    public void updateTimeClassReviewByAddingGame(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate, Player player) {
        Optional<Game> addedGame = gameRepository.findGameByUuid(gameDTOWithZonedTimeDate.getUuid());
        if(addedGame.isEmpty())
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        Optional<GameReview> existingGameReview = gameReviewRepository
                .findAllByUser(player).stream()
                .filter(gameReview -> gameReview.getBestGame().getTimeClass().equals(gameDTOWithZonedTimeDate.getTimeClass()))
                .findAny();
        if(existingGameReview.isEmpty())
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        String gameSide;
        PlayerInGameDTO playerResults;
        if(gameDTOWithZonedTimeDate.getWhitePlayer().getUsername().equals(player.getUsername())) {
            playerResults = gameDTOWithZonedTimeDate.getWhitePlayer();
            gameSide = WHITE;
        }
        else {
            playerResults = gameDTOWithZonedTimeDate.getBlackPlayer();
            gameSide = BLACK;
        }
        changePlayerReviewRecords(existingGameReview.get(), playerResults, ADD);
        if(existingGameReview.get().getBestGame().getWhiteRating() < playerResults.getRating() && gameSide.equals(WHITE) ||
                existingGameReview.get().getBestGame().getBlackRating() < playerResults.getRating() && gameSide.equals(BLACK))
            existingGameReview.get().setBestGame(addedGame.get());
        gameReviewRepository.save(existingGameReview.get());
    }
    @Override
    public void changePlayerReviewRecords(GameReview gameReview, PlayerInGameDTO playerResults, int calledMethod) {
        if(calledMethod < -1 || calledMethod > 1)
            throw new UnsupportedOperationException();
        if(Objects.equals(playerResults.getGameResult(), WIN))
            gameReview.setWinCasesRecord(gameReview.getWinCasesRecord() + calledMethod);
        if(Objects.equals(playerResults.getGameResult(), LOSS) ||
                Objects.equals(playerResults.getGameResult(), CHECKMATED))
            gameReview.setLossCasesRecord(gameReview.getLossCasesRecord() + calledMethod);
        if(Objects.equals(playerResults.getGameResult(), DRAW))
            gameReview.setDrawCasesRecord(gameReview.getDrawCasesRecord() + calledMethod);
    }
    @Override
    public void updateTimeClassReviewByDeletingGame(GameDTOWithZonedTimeDate game, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<GameReview> gameReview = findGameReview(game.getTimeClass(), username);
        if(gameReview.isEmpty())
            throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
        String gameSide;
        PlayerInGameDTO playerResults;
        if(game.getWhitePlayer().getUsername().equals(username)) {
            playerResults = game.getWhitePlayer();
            gameSide = WHITE;
        }
        else {
            playerResults = game.getBlackPlayer();
            gameSide = BLACK;
        }
        changePlayerReviewRecords(gameReview.get(), playerResults, DELETE);
        Optional<Game> bestGame = findBestGame(game, player.get(), gameSide);
        if(bestGame.isEmpty())
            gameReviewRepository.delete(gameReview.get());
        else {
            gameReview.get().setBestGame(bestGame.get());
            gameReviewRepository.save(gameReview.get());
        }
    }
    @Override
    public Optional<Game> findBestGame(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate, Player player, String gameSide) {
        if(gameSide.equals(WHITE))
            return player.getGames().stream()
                    .filter(game -> game.getTimeClass().equals(gameDTOWithZonedTimeDate.getTimeClass()))
                    .filter(game -> !Objects.equals(game.getUuid(), gameDTOWithZonedTimeDate.getUuid()))
                    .max(Comparator.comparingInt(Game::getWhiteRating));
        else
            return player.getGames().stream()
                    .filter(game -> game.getTimeClass().equals(gameDTOWithZonedTimeDate.getTimeClass()))
                    .filter(game -> !Objects.equals(game.getUuid(), gameDTOWithZonedTimeDate.getUuid()))
                    .max(Comparator.comparingInt(Game::getBlackRating));
    }
    @Override
    public Optional<List<GameReviewDTO>> viewPlayerStatistics(String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        return player.map(value -> gameReviewRepository.findAllByUser(value)
                .stream().map(gameReviewDTOMapper).toList());
    }
    @Override
    public Optional<GameReview> findGameReview(String gameType, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        return player.flatMap(value -> value.getGameReviews().stream()
                .filter(gameReview -> gameReview.getBestGame().getTimeClass().equals(gameType))
                .findAny());
    }

    @Override
    public void deleteGameReviewForTimeClass(String timeClass, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<GameReview> gameReview = findGameReview(timeClass, username);
        gameReview.ifPresent(gameReviewRepository::delete);
    }
    @Override
    public void deleteAllReviews() {
        gameReviewRepository.deleteAll();
    }
}
