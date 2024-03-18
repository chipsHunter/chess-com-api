package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.mapper.GameDTOMapper;
import hvorostina.chesscomapi.service.GameService;
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
    private final GameDTOMapper gameDTOMapper;
    @PostMapping("/add")
    public ResponseEntity<GameDTO> addGame(@RequestBody GameDTO gameDTO) {
        try {
            Optional<GameDTO> addedGame = gameService.addGame(gameDTO);
            if(addedGame.isEmpty())
                return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
            return new ResponseEntity<>(gameDTO, HttpStatus.CREATED);
        } catch (HttpClientErrorException exception) {
            return new ResponseEntity<>(null, exception.getStatusCode());
        }
    }
    @GetMapping("/find")
    public ResponseEntity<GameDTO> findGameByUUID(@RequestParam String UUID) {
        Optional<GameDTO> foundGame = gameService.findGameByUUID(UUID);
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
    public HttpStatusCode deleteGame(@RequestParam String UUID) {
        try {
            gameService.deleteGame(UUID);
            return HttpStatus.OK;
        }
        catch (HttpClientErrorException exception) {
            return exception.getStatusCode();
        }
    }
}
