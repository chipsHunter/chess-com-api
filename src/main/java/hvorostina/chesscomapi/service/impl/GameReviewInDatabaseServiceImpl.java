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

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class GameReviewInDatabaseServiceImpl implements GameReviewService {
    private final GameReviewRepository gameReviewRepository;
    private final GameReviewDTOMapper gameReviewDTOMapper;
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    @Override
    public Optional<GameReviewDTO> createTimeClassReview(GameDTO gameDTO, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        if(gameReviewRepository.findGameReviewByGameTypeAndUser(gameDTO.getTimeClass(), player.get()).isPresent())
            return Optional.empty();
        GameReview gameReview = new GameReview();
        gameReview.setGameType(gameDTO.getTimeClass());
        gameReview.setBestGameURL(gameDTO.getGameURL().toString());
        gameReview.setBestGameDate(gameDTO.getGameTimestamp());
        gameReview.setUser(player.get());
        if(Objects.equals(gameDTO.getWhitePlayer().getUsername(), username)) {
            if(Objects.equals(gameDTO.getWhitePlayer().getGameResult(), "win")) {
                gameReview.setWinCasesRecord(1);
                gameReview.setDrawCasesRecord(0);
                gameReview.setLossCasesRecord(0);
            } else if(Objects.equals(gameDTO.getWhitePlayer().getGameResult(), "loss") ||
                    Objects.equals(gameDTO.getWhitePlayer().getGameResult(), "checkmated")) {
                gameReview.setWinCasesRecord(0);
                gameReview.setDrawCasesRecord(0);
                gameReview.setLossCasesRecord(1);
            } else {
                gameReview.setWinCasesRecord(0);
                gameReview.setDrawCasesRecord(1);
                gameReview.setLossCasesRecord(0);
            }
        } else {
            if(Objects.equals(gameDTO.getBlackPlayer().getGameResult(), "win")) {
                gameReview.setWinCasesRecord(1);
                gameReview.setDrawCasesRecord(0);
                gameReview.setLossCasesRecord(0);
            } else if(Objects.equals(gameDTO.getBlackPlayer().getGameResult(), "loss") ||
                    Objects.equals(gameDTO.getBlackPlayer().getGameResult(), "checkmated")) {
                gameReview.setWinCasesRecord(0);
                gameReview.setDrawCasesRecord(0);
                gameReview.setLossCasesRecord(1);
            } else {
                gameReview.setWinCasesRecord(0);
                gameReview.setDrawCasesRecord(1);
                gameReview.setLossCasesRecord(0);
            }
        }
        return Optional.of(gameReviewDTOMapper.apply(gameReviewRepository.save(gameReview)));
    }

    @Override
    public Optional<GameReviewDTO> updateTimeClassReviewWithGame(GameDTO gameDTO, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        Optional<GameReview> gameReview = gameReviewRepository.findGameReviewByGameTypeAndUser(gameDTO.getTimeClass(), player.get());
        if(gameReview.isEmpty())
            return Optional.empty();
        if(Objects.equals(gameDTO.getWhitePlayer().getUsername(), username)) {
            if(Objects.equals(gameDTO.getWhitePlayer().getGameResult(), "win")) {
                gameReview.get().setWinCasesRecord(gameReview.get().getWinCasesRecord() + 1);
                Optional<Game> oldBestGame = gameRepository.findGameByGameURL(gameReview.get().getBestGameURL());
                if(oldBestGame.isEmpty())
                    throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
                if(oldBestGame.get().getWhiteRating() < gameDTO.getWhitePlayer().getRating()) {
                    gameReview.get().setBestGameURL(gameDTO.getGameURL().toString());
                    gameReview.get().setBestGameDate(gameDTO.getGameTimestamp());
                }
            } else if(Objects.equals(gameDTO.getWhitePlayer().getGameResult(), "loss") ||
                    Objects.equals(gameDTO.getWhitePlayer().getGameResult(), "checkmated")) {
                gameReview.get().setLossCasesRecord(gameReview.get().getLossCasesRecord() + 1);
            } else gameReview.get().setDrawCasesRecord(gameReview.get().getDrawCasesRecord() + 1);
        } else {
            if(Objects.equals(gameDTO.getBlackPlayer().getGameResult(), "win")) {
                gameReview.get().setWinCasesRecord(gameReview.get().getWinCasesRecord() + 1);
                Optional<Game> oldBestGame = gameRepository.findGameByGameURL(gameReview.get().getBestGameURL());
                if(oldBestGame.isEmpty())
                    throw new HttpServerErrorException(HttpStatus.BAD_REQUEST);
                if(oldBestGame.get().getBlackRating() < gameDTO.getBlackPlayer().getRating()) {
                    gameReview.get().setBestGameURL(gameDTO.getGameURL().toString());
                    gameReview.get().setBestGameDate(Timestamp.valueOf(gameDTO.getGameData()));
                }
            } else if(Objects.equals(gameDTO.getBlackPlayer().getGameResult(), "loss") ||
                    Objects.equals(gameDTO.getBlackPlayer().getGameResult(), "checkmated")) {
                gameReview.get().setLossCasesRecord(gameReview.get().getLossCasesRecord() + 1);
            } else gameReview.get().setDrawCasesRecord(gameReview.get().getDrawCasesRecord() + 1);
        }
        return Optional.of(gameReviewDTOMapper.apply(gameReviewRepository.save(gameReview.get())));
    }

    @Override
    public List<GameReviewDTO> viewPlayerStatistics(String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        return player.map(value -> gameReviewRepository.findAllByUser(value)
                .stream().map(gameReviewDTOMapper).collect(Collectors.toList())).orElseGet(List::of);
    }
    @Override
    public Optional<GameReviewDTO> findGameReview(String gameType, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        return player.flatMap(value -> gameReviewRepository
                .findGameReviewByGameTypeAndUser(gameType, value)
                .map(gameReviewDTOMapper));
    }

    @Override
    public void deleteGameReviewForTimeClass(String timeClass, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<GameReview> gameReview = gameReviewRepository.findGameReviewByGameTypeAndUser(timeClass, player.get());
        gameReview.ifPresent(gameReviewRepository::delete);
    }
    @Override
    public void deleteAllReviews() {
        gameReviewRepository.deleteAll();
    }
    @Override
    public Optional<GameReviewDTO> updateTimeClassReviewByDeletingGame(GameDTO game, String username) {
        Optional<Player> player = playerRepository.findPlayerByUsername(username);
        if(player.isEmpty())
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        Optional<GameReview> updatedGameReview = gameReviewRepository.findGameReviewByGameTypeAndUser(game.getTimeClass(), player.get());
        if(updatedGameReview.isEmpty())
            return Optional.empty();
        PlayerInGameDTO playerResults;
        String playerSide;
        if(Objects.equals(game.getWhitePlayer().getUsername(), username)) {
            playerResults = PlayerInGameDTO.builder()
                    .gameResult(game.getWhitePlayer().getGameResult())
                    .rating(game.getWhitePlayer().getRating())
                    .username(username)
                    .build();
            playerSide = "white";
        }
        else {
            playerResults = PlayerInGameDTO.builder()
                    .gameResult(game.getBlackPlayer().getGameResult())
                    .rating(game.getBlackPlayer().getRating())
                    .username(username)
                    .build();
            playerSide = "black";
        }
        if(Objects.equals(playerResults.getGameResult(), "win")) {
            updatedGameReview.get().setWinCasesRecord(updatedGameReview.get().getWinCasesRecord() - 1);
        }
        if(Objects.equals(playerResults.getGameResult(), "loss") ||
                Objects.equals(playerResults.getGameResult(), "checkmated")) {
            updatedGameReview.get().setLossCasesRecord(updatedGameReview.get().getLossCasesRecord() - 1);
        }
        if(Objects.equals(playerResults.getGameResult(), "draw")) {
            updatedGameReview.get().setDrawCasesRecord(updatedGameReview.get().getDrawCasesRecord() - 1);
        }
        if(updatedGameReview.get().getBestGameURL().equals(game.getGameURL().toString())) {
            Optional<Game> gameRes;
            if(playerSide.equals("black"))
                gameRes = player.get().getGames().stream().max(Comparator.comparingInt(Game::getBlackRating));
            else gameRes = player.get().getGames().stream().max(Comparator.comparingInt(Game::getWhiteRating));
            if(gameRes.isEmpty()) {
                Optional<GameReview> gameRev = player.get()
                        .getGameReviews().stream().parallel()
                        .filter(gameReview -> gameReview.getGameType().equals(game.getTimeClass()))
                        .findAny();
                if(gameRev.isEmpty())
                    throw new HttpServerErrorException(HttpStatus.CONFLICT);
                player.get().getGameReviews().remove(gameRev.get());
                gameReviewRepository.delete(gameRev.get());
                return Optional.empty();
            }
            updatedGameReview.get().setBestGameDate(gameRes.get().getData());
            updatedGameReview.get().setBestGameURL(gameRes.get().getGameURL());
        }
        return Optional.of(gameReviewDTOMapper.apply(gameReviewRepository.save(updatedGameReview.get())));
    }
}
