package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.GameReviewService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/database/game_review")
public class DatabaseGameReviewController {
    private final GameReviewService gameReviewService;
    private final PlayerRepository playerRepository;
    @GetMapping("/find_all")
    List<GameReviewDTO> checkPlayerStatistics(@RequestParam String username) {
        if(playerRepository.findPlayerByUsername(username).isEmpty())
            return List.of();
        return gameReviewService.viewPlayerStatistics(username);
    }
    @PatchMapping("/patch")
    public ResponseEntity<String> patchPlayerStatistics(@RequestBody GameDTO game) {
        return new ResponseEntity<>("You can't call this method", HttpStatus.METHOD_NOT_ALLOWED);
    }
    @PostMapping("/add")
    public ResponseEntity<String> addPlayerStatistics(@RequestBody GameDTO game) {
        return new ResponseEntity<>("You can't call this method", HttpStatus.METHOD_NOT_ALLOWED);
    }
    @DeleteMapping("/delete")
    public HttpStatus deletePlayerStatistics(@RequestParam String gameType, @RequestParam String username) {
        return HttpStatus.METHOD_NOT_ALLOWED;
    }
    @DeleteMapping("/test/delete_all")
    public HttpStatus deleteAllPlayersStatistics() {
        gameReviewService.deleteAllReviews();
        return HttpStatus.OK;
    }
}
