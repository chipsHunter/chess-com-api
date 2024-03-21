package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;

import java.util.List;
import java.util.Optional;

public interface GameReviewService {
    void createTimeClassReview(GameDTO gameDTO, Player player);
    void updateTimeClassReviewByAddingGame(GameReview gameReview, GameDTO gameDTO, Player player);
    void updateTimeClassReviewByDeletingGame(GameDTO gameDTO, String username);
    Optional<List<GameReviewDTO>> viewPlayerStatistics(String username);
    Optional<GameReview> findGameReview(String gameType, String username);
    void deleteAllReviews();
    void changePlayerReviewRecords(GameReview gameReview, PlayerInGameDTO results, int calledMethod);
    void manageGameReviewForNewGame(GameDTO gameDTO);
    public void deleteGameReviewForTimeClass(String timeClass, String username);
    Optional<Game> findBestGame(GameDTO gameDTO, Player player, String gameSide);
}
