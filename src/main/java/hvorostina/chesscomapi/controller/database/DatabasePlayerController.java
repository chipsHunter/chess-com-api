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

@RestController
@AllArgsConstructor
@RequestMapping("/database/player")
public class DatabasePlayerController {
    private final PlayerService playerService;
    @GetMapping("/find_all")
    public List<PlayerDTO> findAllUsers() {
        return playerService.findAllPlayers();
    }
    @GetMapping("/find")
    public ResponseEntity<String> findUserByUsername(@RequestParam String username) {
        Optional<PlayerDTO> player = playerService.findPlayerByUsername(username.toLowerCase());
        return player.map(playerDTO ->
                new ResponseEntity<>(playerDTO.toString(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("Nothing was found!", HttpStatus.NOT_FOUND));
    }
    @PostMapping("/add")
    public ResponseEntity<String> addUser(@RequestBody PlayerDTO playerDTO) {
        Player player = new Player();
        player.setPlayerID(playerDTO.getPlayerID());
        player.setStatus(playerDTO.getStatus());
        player.setCountry(playerDTO.getCountry());
        player.setUsername(playerDTO.getUsername().toLowerCase());
        Optional<PlayerDTO> savedPlayer = playerService.addPlayer(player);
        if(savedPlayer.isEmpty())
            return new ResponseEntity<>("User already exists!", HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(playerDTO.toString(), HttpStatus.CREATED);
    }
    @PatchMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody PlayerDTO playerDTO) {
        Optional<PlayerDTO> result = playerService.updatePlayer(playerDTO);
        return result.map(dto -> new ResponseEntity<>(dto.toString(), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>("There's no user with username" + playerDTO.getUsername(), HttpStatus.NOT_FOUND));
    }
    @DeleteMapping("/delete")
    public HttpStatus deleteUser(@RequestParam String username) {
        try {
            playerService.deletePlayerByUsername(username.toLowerCase());
        } catch (HttpClientErrorException exception) {
            return HttpStatus.BAD_REQUEST;
        }
        return HttpStatus.OK;
    }
}
