package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.model.dto.PlayerWithGamesDTO;
import hvorostina.chesscomapi.model.mapper.PlayerMapper;
import hvorostina.chesscomapi.service.GameReviewService;
import hvorostina.chesscomapi.service.PlayerService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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
    public ResponseEntity<PlayerDTO> findUserByUsername(
            final @RequestParam String username) {
        PlayerDTO player = playerService
                .findPlayerByUsernameAndSaveInCache(username);
        return new ResponseEntity<>(player, HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<PlayerDTO> addUser(
            final @Valid @RequestBody PlayerDTO playerDTO) {
        Player player = playerMapper.apply(playerDTO);
        PlayerDTO savedPlayer = playerService.addPlayer(player);
        return new ResponseEntity<>(savedPlayer, HttpStatus.CREATED);
    }
    @PatchMapping("/update")
    public ResponseEntity<PlayerDTO> updateUser(
            final @RequestBody PlayerDTO playerDTO)
            throws HttpClientErrorException {
        PlayerDTO updatedPlayer = playerService
                .updatePlayerAndSaveInCache(playerDTO);
        if (updatedPlayer == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(updatedPlayer, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public HttpStatus deleteUser(
            final @RequestParam String username)
            throws HttpClientErrorException {
        Player player = playerService.findPlayerEntityByUsername(username);
        if (player == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        gameReviewService.deleteAllReviewsByPlayer(player);
        playerService.deletePlayer(player);
        return HttpStatus.OK;
    }
    @GetMapping("/all_players_with_games")
    public List<PlayerWithGamesDTO> getAllPlayersWithGames() {
        return playerService.getAllPlayersWithGames();
    }
    @GetMapping("/internal_server_error")
    public void internalServerErrorException() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
