package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.model.dto.PlayerWithGamesDTO;
import hvorostina.chesscomapi.model.mapper.PlayerMapper;
import hvorostina.chesscomapi.service.GameReviewService;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/database/player")
public class DatabasePlayerController {
    private final PlayerService playerService;
    private final GameReviewService gameReviewService;
    private final PlayerMapper playerMapper;
    @GetMapping("/find_all")
    public List<PlayerDTO> findAllUsers() {
        return playerService.findAllPlayers();
    }
    @GetMapping("/find")
    public ResponseEntity<PlayerDTO> findUserByUsername(@RequestParam String username) {
        PlayerDTO player = playerService.findPlayerByUsernameAndSaveInCache(username);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<PlayerDTO> addUser(@RequestBody PlayerDTO playerDTO) {
        Player player = playerMapper.apply(playerDTO);
        PlayerDTO savedPlayer = playerService.addPlayer(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
    }
    @PatchMapping("/update")
    public ResponseEntity<PlayerDTO> updateUser(@RequestBody PlayerDTO playerDTO) {
        PlayerDTO updatedPlayer = playerService.updatePlayerAndSaveInCache(playerDTO);
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public HttpStatus deleteUser(@RequestParam String username) {
        Player player = playerService.findPlayerEntityByUsername(username);
        if(player == null)
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        gameReviewService.deleteAllReviewsByPlayer(player);
        playerService.deletePlayerByUsername(username);
        return HttpStatus.OK;
    }
    @GetMapping("/all_players_with_games")
    public List<PlayerWithGamesDTO> getAllPlayersWithGames() {
        return playerService.getAllPlayersWithGames();
    }
}
