package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.model.dto.PlayerDTO;

import java.util.List;
import java.util.Optional;

public interface GameReviewService {
    Optional<GameReviewDTO> createTimeClassReview(GameDTO gameDTO, String username);
    Optional<GameReviewDTO> updateTimeClassReviewWithGame(GameDTO gameDTO, String username);
    Optional<GameReviewDTO> updateTimeClassReviewByDeletingGame(GameDTO gameDTO, String username);
    List<GameReviewDTO> viewPlayerStatistics(String username);
    Optional<GameReviewDTO> findGameReview(String gameType, String username);
    void deleteAllReviews();
    public void deleteGameReviewForTimeClass(String timeClass, String username);
}
