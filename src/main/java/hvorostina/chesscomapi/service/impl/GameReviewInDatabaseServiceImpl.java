package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
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
    private static final String CHECKMATED = "CHECKMATED";
    private static final String WIN = "WIN";
    private static final String LOSS = "LOSS";
    private static final String DRAW = "DRAW";
    private static final String WHITE = "WHITE";
    private static final String BLACK = "BLACK";
    private static final int ADD = 1;
    private static final int DELETE = -1;
    @Override
    public void manageGameReviewForNewGame(GameDTO gameDTO) {
        Optional<Player> whitePlayer = playerRepository.findPlayerByUsername(gameDTO.getWhitePlayer().getUsername());
        if(whitePlayer.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<Player> blackPlayer = playerRepository.findPlayerByUsername(gameDTO.getBlackPlayer().getUsername());
        if(blackPlayer.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<GameReview> existingGameReview = gameReviewRepository
                .findAllByUser(whitePlayer.get()).stream()
                .filter(gameReview -> gameReview.getBestGame().getTimeClass().equals(gameDTO.getTimeClass()))
                .findAny();
        if(existingGameReview.isEmpty()) {
            createTimeClassReview(gameDTO, whitePlayer.get());
            createTimeClassReview(gameDTO, blackPlayer.get());
        } else {
            updateTimeClassReviewByAddingGame(existingGameReview.get(), gameDTO, whitePlayer.get());
            updateTimeClassReviewByAddingGame(existingGameReview.get(), gameDTO, blackPlayer.get());
        }
    }
    @Override
    public void createTimeClassReview(GameDTO gameDTO, Player player) {
        Optional<Game> game = gameRepository.findGameByUuid(gameDTO.getUuid());
        if(game.isEmpty())
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        PlayerInGameDTO playerResults;
        if(gameDTO.getWhitePlayer().getUsername().equals(player.getUsername()))
            playerResults = gameDTO.getWhitePlayer();
        else
            playerResults = gameDTO.getBlackPlayer();
        GameReview gameReview = new GameReview();
        gameReview.setBestGame(game.get());
        gameReview.setUser(player);
        changePlayerReviewRecords(gameReview, playerResults, ADD);
        gameReviewRepository.save(gameReview);
    }
    @Override
    public void updateTimeClassReviewByAddingGame(GameReview gameReview, GameDTO game, Player player) {
        Optional<Game> addedGame = gameRepository.findGameByUuid(game.getUuid());
        if(addedGame.isEmpty())
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        String gameSide;
        PlayerInGameDTO playerResults;
        if(game.getWhitePlayer().getUsername().equals(player.getUsername())) {
            playerResults = game.getWhitePlayer();
            gameSide = WHITE;
        }
        else {
            playerResults = game.getBlackPlayer();
            gameSide = BLACK;
        }
        changePlayerReviewRecords(gameReview, playerResults, ADD);
        if(gameReview.getBestGame().getWhiteRating() < playerResults.getRating() && gameSide.equals(WHITE) ||
                gameReview.getBestGame().getBlackRating() < playerResults.getRating() && gameSide.equals(BLACK))
            gameReview.setBestGame(addedGame.get());
        gameReviewRepository.save(gameReview);
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
    public void updateTimeClassReviewByDeletingGame(GameDTO game, String username) {
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
    public Optional<Game> findBestGame(GameDTO gameDTO, Player player, String gameSide) {
        if(gameSide.equals(WHITE))
            return player.getGames().stream()
                    .filter(game -> game.getTimeClass().equals(gameDTO.getTimeClass()))
                    .filter(game -> !Objects.equals(game.getUuid(), gameDTO.getUuid()))
                    .max(Comparator.comparingInt(Game::getWhiteRating));
        else
            return player.getGames().stream()
                    .filter(game -> game.getTimeClass().equals(gameDTO.getTimeClass()))
                    .filter(game -> !Objects.equals(game.getUuid(), gameDTO.getUuid()))
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
