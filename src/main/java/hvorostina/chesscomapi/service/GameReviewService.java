package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;

import java.util.List;

public interface GameReviewService {
    List<GameReview> viewPlayerStatistics(Player player);

    void deleteAllReviews();
    GameReview manageGameReviewWhenAddGame(
            GameDTOWithDate game, Player player);
    void manageGameReviewWhenDeleteGame(GameDTOWithDate game, Player player);

    void deleteAllReviewsByPlayer(Player player);

}
