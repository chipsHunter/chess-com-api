package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameReviewDTO;
import hvorostina.chesscomapi.model.mapper.GameDTOMapper;
import hvorostina.chesscomapi.service.GameReviewService;
import hvorostina.chesscomapi.service.GameService;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("/database/game")
public class DatabaseGameController {
    private final GameService gameService;
    private final GameReviewService gameReviewService;
    private final PlayerService playerService;
    @PostMapping("/add")
    public ResponseEntity<GameDTO> addGame(@RequestBody GameDTO gameDTO) {
        Optional<GameDTO> addedGame = gameService.addGame(gameDTO);
        if(addedGame.isEmpty())
            return new ResponseEntity<>(gameDTO, HttpStatus.FOUND);
        gameReviewService.manageGameReviewForNewGame(gameDTO);
        return new ResponseEntity<>(gameDTO, HttpStatus.CREATED);
    }
    @GetMapping("/find")
    public ResponseEntity<GameDTO> findGameByUUID(@RequestParam String uuid) {
        Optional<GameDTO> foundGame = gameService.findGameByUUID(uuid);
        return foundGame.map(gameDTO ->
                new ResponseEntity<>(gameDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
    @PatchMapping("/update")
    public ResponseEntity<GameDTO> updateGame(@RequestBody GameDTO gameDTO) {
        Optional<GameDTO> updateGame = gameService.updateGameResult(gameDTO);
        return updateGame.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
    @GetMapping("/find_all")
    public List<GameDTO> findAllGames() {
        return gameService.findAllGames();
    }
    @DeleteMapping("/delete")
    public HttpStatusCode deleteGame(@RequestParam String uuid) {
        try {
            Optional<GameDTO> gameDTO = gameService.findGameByUUID(uuid);
            if(gameDTO.isEmpty())
                return HttpStatus.BAD_REQUEST;
            gameReviewService.updateTimeClassReviewByDeletingGame(gameDTO.get(), gameDTO.get().getWhitePlayer().getUsername());
            gameReviewService.updateTimeClassReviewByDeletingGame(gameDTO.get(), gameDTO.get().getBlackPlayer().getUsername());
            gameService.deleteGame(uuid);
            return HttpStatus.OK;
        }
        catch (HttpClientErrorException exception) {
            return exception.getStatusCode();
        }
    }
    @DeleteMapping("/delete_all")
    public HttpStatus deleteAllGames() {
        gameReviewService.deleteAllReviews();
        gameService.deleteAllGames();
        return HttpStatus.OK;
    }
}
