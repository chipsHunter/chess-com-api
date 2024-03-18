package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/database/player")
public class DatabasePlayerController {
    private final PlayerService playerService;
    @GetMapping("/find_all")
    public List<PlayerDTO> findAllUsers() {
        return playerService.findAllPlayers();
    }
    @GetMapping("/find_user")
    public ResponseEntity<PlayerDTO> findUserByUsername(@RequestParam String username) {
        Optional<PlayerDTO> player = playerService.findPlayerByUsername(username);
        return player.map(playerDTO -> new ResponseEntity<>(playerDTO, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
    @PostMapping("/add_user")
    public ResponseEntity<PlayerDTO> addUser(@RequestBody PlayerDTO playerDTO) {
        Player player = new Player();
        player.setPlayerID(playerDTO.getPlayerID());
        player.setStatus(playerDTO.getStatus());
        player.setCountry(playerDTO.getCountry());
        player.setUsername(playerDTO.getUsername());
        Optional<PlayerDTO> savedPlayer = playerService.addPlayer(player);
        if(savedPlayer.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(playerDTO, HttpStatus.CREATED);
    }
    @PatchMapping("/update_user")
    public ResponseEntity<PlayerDTO> updateUser(@RequestBody PlayerDTO playerDTO) {
        Optional<PlayerDTO> result = playerService.updatePlayer(playerDTO);
        return result.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/delete_user")
    public HttpStatus deleteUser(@RequestParam String username) {
        try {
            playerService.deletePlayerByUsername(username);
        } catch (HttpClientErrorException exception) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
}
