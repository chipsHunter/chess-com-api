package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;

import java.util.List;

public interface GameReviewService {
    public List<GameReviewDTO> viewPlayerStatistics(Player player);

    void deleteAllReviews();
    GameReviewDTO manageGameReviewWhenAddGame(GameDTOWithDate game, Player player);
    void manageGameReviewWhenDeleteGame(GameDTOWithDate game, Player player);

    void deleteAllReviewsByPlayer(Player player);

}
