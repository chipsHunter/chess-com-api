package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.model.mapper.GameReviewDTOMapper;
import hvorostina.chesscomapi.service.GameReviewService;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/database/game_review")
public class DatabaseGameReviewController {
    private final GameReviewService gameReviewService;
    private final PlayerService playerService;
    private final GameReviewDTOMapper reviewDTOMapper;
    @GetMapping("/find_all")
    public List<GameReviewDTO> checkPlayerStatistics(
            final @RequestParam String username) {
        Player player = playerService.findPlayerEntityByUsername(username);
        if (player == null) {
            return List.of();
        }
        List<GameReview> playerStatistics = gameReviewService.viewPlayerStatistics(player);
        return  playerStatistics.stream()
                .map(reviewDTOMapper)
                .toList();
    }
    @PatchMapping("/patch")
    public ResponseEntity<String> patchPlayerStatistics(
            final @RequestBody GameDTO game) {
        return new ResponseEntity<>("You can't call this method",
                HttpStatus.METHOD_NOT_ALLOWED);
    }
    @PostMapping("/add")
    public ResponseEntity<String> addPlayerStatistics(
            final @RequestBody GameDTO game) {
        return new ResponseEntity<>("You can't call this method",
                HttpStatus.METHOD_NOT_ALLOWED);
    }
    @DeleteMapping("/delete")
    public HttpStatus deletePlayerStatistics(
            final @RequestParam String gameType,
            final @RequestParam String username) {
        return HttpStatus.METHOD_NOT_ALLOWED;
    }
    @DeleteMapping("/test/delete_all")
    public HttpStatus deleteAllPlayersStatistics() {
        gameReviewService.deleteAllReviews();
        return HttpStatus.OK;
    }
}
