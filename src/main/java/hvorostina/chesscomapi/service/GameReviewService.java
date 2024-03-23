package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithZonedTimeDate;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;

import java.util.List;
import java.util.Optional;

public interface GameReviewService {
    void createTimeClassReview(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate, Player player);
    void updateTimeClassReviewByAddingGame(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate, Player player);
    void updateTimeClassReviewByDeletingGame(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate, String username);
    Optional<List<GameReviewDTO>> viewPlayerStatistics(String username);
    Optional<GameReview> findGameReview(String gameType, String username);
    void deleteAllReviews();
    void changePlayerReviewRecords(GameReview gameReview, PlayerInGameDTO results, int calledMethod);
    void manageGameReviewForNewGame(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate);
    public void deleteGameReviewForTimeClass(String timeClass, String username);
    Optional<Game> findBestGame(GameDTOWithZonedTimeDate gameDTOWithZonedTimeDate, Player player, String gameSide);
}
