package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.annotations.AspectAnnotation;
import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import hvorostina.chesscomapi.model.mapper.GameDTOWithDateMapper;
import hvorostina.chesscomapi.model.mapper.GameReviewDTOMapper;
import hvorostina.chesscomapi.repository.GameRepository;
import hvorostina.chesscomapi.repository.GameReviewRepository;
import hvorostina.chesscomapi.service.GameReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
public class GameReviewInDatabaseServiceImpl implements GameReviewService {
    private final GameReviewRepository gameReviewRepository;
    private final GameReviewDTOMapper gameReviewDTOMapper;
    private final GameRepository gameRepository;
    private final GameDTOWithDateMapper gameDTOMapper;
    private static final String CHECKMATED = "checkmated";
    private static final String WIN = "win";
    private static final String LOSS = "loss";
    private static final String DRAW = "draw";
    private static final String WHITE = "white";
    private static final String BLACK = "black";
    private static final int ADD = 1;
    private static final int DELETE = -1;
    @Override
    @AspectAnnotation
    public List<GameReviewDTO> viewPlayerStatistics(final Player player) {
        List<GameReview> allReviews = player.getGameReviews();
        return allReviews.stream()
                .map(gameReviewDTOMapper)
                .toList();
    }
    @Override
    @AspectAnnotation
    public GameReviewDTO manageGameReviewWhenAddGame(
            final GameDTOWithDate game, final Player player) {
        String playerSide = getPlayerSide(game, player);
        PlayerInGameDTO playerResults = playerResults(game, playerSide);
        Optional<GameReview> playerReview =
                getPlayerReviewForTimeClass(player, game.getTimeClass());
        if (playerReview.isPresent()) {
            return updateGameReviewWhenAddGame(
                    playerReview.get(), game, playerResults);
        }
        return createGameReview(game, player, playerResults);
    }
    @AspectAnnotation
    public void manageGameReviewWhenDeleteGame(
            final GameDTOWithDate game, final Player player) {
        Optional<GameReview> optionalPlayerReview =
                getPlayerReviewForTimeClass(player, game.getTimeClass());
        if (optionalPlayerReview.isEmpty()) {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        GameReview playerReview = optionalPlayerReview.get();
        List<GameDTOWithDate> gamesWithTimeClass =
                playerGamesWithTimeClass(player, game.getTimeClass());
        if (lastGameInTimeClass(gamesWithTimeClass)) {
            gameReviewRepository.delete(playerReview);
            return;
        }
        String playerSide = getPlayerSide(game, player);
        PlayerInGameDTO playerResults = playerResults(game, playerSide);
        setGameStat(playerReview, playerResults, DELETE);
        setPreviousGame(playerReview, gamesWithTimeClass);
        gameReviewRepository.save(playerReview);
        gameReviewDTOMapper.apply(playerReview);
    }
    @Override
    @AspectAnnotation
    public void deleteAllReviewsByPlayer(final Player player) {
        List<GameReview> reviews = player.getGameReviews();
        gameReviewRepository.deleteAll(reviews);
    }
    private void setPreviousGame(
            final GameReview review,
            final List<GameDTOWithDate> allGames) {
        String previousBestGameUuid =
                getPreviousBestGameUuid(review, allGames);
        Optional<Game> bestPrevGame = gameRepository
                .findGameByUuid(previousBestGameUuid);
        if (bestPrevGame.isEmpty()) {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        review.setBestGame(bestPrevGame.get());
    }
    private String getPreviousBestGameUuid(
            final GameReview review,
            final List<GameDTOWithDate> allGames) {
        Player player = review.getUser();
        GameDTOWithDate bestGame = gameDTOMapper
                .apply(review.getBestGame());
        GameDTOWithDate prevBestGameDTO = allGames.get(0);
        for (GameDTOWithDate game: allGames) {
            if (isSuccessful(game, prevBestGameDTO, player)
                    && notEqualGames(game, bestGame)) {
                prevBestGameDTO = game;
            }
        }
        return prevBestGameDTO.getUuid();
    }
    private boolean notEqualGames(
            final GameDTOWithDate first, final GameDTOWithDate second) {
        String firstUuid = first.getUuid();
        String secondUuid = second.getUuid();
        return !Objects.equals(firstUuid, secondUuid);
    }
    private boolean isSuccessful(
            final GameDTOWithDate first,
            final GameDTOWithDate second,
            final Player player) {
        int firstRating = getPlayerRating(first, player);
        int secondRating = getPlayerRating(second, player);
        return firstRating > secondRating;
    }

    private int getPlayerRating(
            final GameDTOWithDate first, final Player player) {
        String playerSide = getPlayerSide(first, player);
        PlayerInGameDTO playerResults = playerResults(first, playerSide);
        return playerResults.getRating();
    }

    private boolean lastGameInTimeClass(
            final List<GameDTOWithDate> gamesWithTimeClass) {
        return gamesWithTimeClass.size() == 1;
    }
    private List<GameDTOWithDate> playerGamesWithTimeClass(
            final Player player, final String timeClass) {
        List<Game> games = player.getGames();
        return games.stream()
                .filter(game -> isSuitableTimeClass(game, timeClass))
                .map(gameDTOMapper)
                .toList();
    }

    private boolean isSuitableTimeClass(
            final Game game, final String timeClass) {
        return game.getTimeClass().equals(timeClass);
    }
    private String getPlayerSide(
            final GameDTOWithDate game, final Player player) {
        String playerUsername = player.getUsername();
        String whiteUsername = game.getWhitePlayer().getUsername();
        if (playerUsername.equals(whiteUsername)) {
            return WHITE;
        }
        return BLACK;
    }
    private Optional<GameReview> getPlayerReviewForTimeClass(
            final Player player, final String timeClass) {
        return player.getGameReviews().stream()
                .filter(gameReview1 -> gameReview1
                        .getTimeClass().equals(timeClass))
                .findAny();
    }
    private boolean gameIsBest(
            final GameReview review,
            final PlayerInGameDTO playerGameResults) {
        int previousRating = review.getBestRating();
        int currentRating = playerGameResults.getRating();
        return currentRating > previousRating;
    }
    private PlayerInGameDTO playerResults(
            final GameDTOWithDate game, final String side) {
        if (side.equals(WHITE)) {
            return game.getWhitePlayer();
        }
        return game.getBlackPlayer();
    }
    private GameReviewDTO updateGameReviewWhenAddGame(
            final GameReview review,
            final GameDTOWithDate game,
            final PlayerInGameDTO playerResults) {
        setGameStat(review, playerResults, ADD);
        setBestGame(review, game, playerResults);
        gameReviewRepository.save(review);
        return gameReviewDTOMapper.apply(review);
    }

    private void setBestGame(final GameReview review,
                             final GameDTOWithDate game,
                             final PlayerInGameDTO playerGameResults) {
        Optional<Game> gameByUuid = gameRepository
                .findGameByUuid(game.getUuid());
        if (gameByUuid.isEmpty()) {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (gameIsBest(review, playerGameResults)) {
            review.setBestGame(gameByUuid.get());
        }
    }

    private GameReviewDTO createGameReview(
            final GameDTOWithDate game,
            final Player player,
            final PlayerInGameDTO playerResults) {
        GameReview gameReview = new GameReview();
        gameReview.setUser(player);
        gameReview.setTimeClass(game.getTimeClass());
        setGameStat(gameReview, playerResults, ADD);
        setBestGame(gameReview, game, playerResults);
        gameReviewRepository.save(gameReview);
        return gameReviewDTOMapper.apply(gameReview);
    }
    private void setGameStat(final GameReview review,
                             final PlayerInGameDTO playerGameResults,
                             final int method) {
        if (playerGameResults.getGameResult().equals(WIN)) {
            int wins = review.getWinCasesRecord();
            review.setWinCasesRecord(wins + method);
        }
        if (playerGameResults.getGameResult().equals(LOSS)
                || playerGameResults.getGameResult().equals(CHECKMATED)) {
            int loss = review.getLossCasesRecord();
            review.setWinCasesRecord(loss + method);
        }
        if (playerGameResults.getGameResult().equals(DRAW)) {
            int draws = review.getDrawCasesRecord();
            review.setWinCasesRecord(draws + method);
        }
    }
    @AspectAnnotation
    public void deleteAllReviews() {
        gameReviewRepository.deleteAll();
    }
}
