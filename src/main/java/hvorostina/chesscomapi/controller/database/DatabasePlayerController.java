package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.service.GameReviewService;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/database/player")
public class DatabasePlayerController {
    private final PlayerService playerService;
    private final GameReviewService gameReviewService;
    @GetMapping("/find_all")
    public List<PlayerDTO> findAllUsers() {
        return playerService.findAllPlayers();
    }
    @GetMapping("/find")
    public ResponseEntity<PlayerDTO> findUserByUsername(@RequestParam String username) {
        Optional<PlayerDTO> player = playerService.findPlayerByUsername(username);
        return player.map(playerDTO ->
                new ResponseEntity<>(playerDTO, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
    @PostMapping("/add")
    public ResponseEntity<PlayerDTO> addUser(@RequestBody PlayerDTO playerDTO) {
        Player player = new Player();
        player.setId(playerDTO.getPlayerID());
        player.setStatus(playerDTO.getStatus());
        player.setCountry(playerDTO.getCountry());
        player.setUsername(playerDTO.getUsername().toLowerCase());
        Optional<PlayerDTO> savedPlayer = playerService.addPlayer(player);
        if(savedPlayer.isEmpty())
            return ResponseEntity.status(HttpStatus.FOUND).body(null);
        return new ResponseEntity<>(playerDTO, HttpStatus.CREATED);
    }
    @PatchMapping("/update")
    public ResponseEntity<PlayerDTO> updateUser(@RequestBody PlayerDTO playerDTO) {
        Optional<PlayerDTO> result = playerService.updatePlayer(playerDTO);
        return result.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }
    @DeleteMapping("/delete")
    public HttpStatus deleteUser(@RequestParam String username) {
        Optional<PlayerDTO> player = playerService.findPlayerByUsername(username);
        if(player.isEmpty())
            return HttpStatus.BAD_REQUEST;
        gameReviewService.deleteAllReviewsByUsername(username);
        playerService.deletePlayerByUsername(username);
        return HttpStatus.OK;
    }
}
