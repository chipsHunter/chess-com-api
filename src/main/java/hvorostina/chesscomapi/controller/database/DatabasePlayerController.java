package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.PlayerDTO;
import hvorostina.chesscomapi.model.dto.PlayerWithGamesDTO;
import hvorostina.chesscomapi.model.mapper.PlayerDTOMapper;
import hvorostina.chesscomapi.model.mapper.PlayerMapper;
import hvorostina.chesscomapi.model.mapper.PlayerWithGamesDTOMapper;
import hvorostina.chesscomapi.service.GameReviewService;
import hvorostina.chesscomapi.service.PlayerService;
import jakarta.validation.Valid;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/database/player")
public class DatabasePlayerController {
    private final PlayerService playerService;
    private final GameReviewService gameReviewService;
    private final PlayerDTOMapper playerDTOMapper;
    private final PlayerMapper playerMapper;
    private final PlayerWithGamesDTOMapper playerWithGamesDTOMapper;
    @GetMapping("/find_all")
    public List<PlayerDTO> findAllUsers() {
        List<Player> players = playerService.findAllPlayers();
        return players.stream()
                .map(playerDTOMapper)
                .toList();
    }
    @PostMapping("/save_player_collection")
    public List<PlayerDTO> savePlayerCollection(
            final @RequestBody List<PlayerDTO> playersDTOs) {
        if(playersDTOs.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        List<Player> players = playersDTOs.stream()
                .map(playerMapper)
                .toList();
        List<Player> savedPlayers = playerService
                .bulkInsertPlayers(players);
        return savedPlayers.stream()
                .map(playerDTOMapper)
                .toList();
    }
    @GetMapping("/find")
    public ResponseEntity<PlayerDTO> findUserByUsername(
            final @RequestParam String username) {
        Player player = playerService
                .findPlayerByUsernameAndSaveInCache(username);
        PlayerDTO playerDTO = playerDTOMapper.apply(player);
        return new ResponseEntity<>(playerDTO, HttpStatus.OK);
    }
    @PostMapping("/add")
    public ResponseEntity<PlayerDTO> addUser(
            final @Valid @RequestBody PlayerDTO playerDTO) {
        Player player = playerMapper.apply(playerDTO);
        playerService.addPlayer(player);
        return new ResponseEntity<>(playerDTO, HttpStatus.CREATED);
    }
    @PatchMapping("/update")
    public ResponseEntity<PlayerDTO> updateUser(
            final @RequestBody PlayerDTO fields)
            throws HttpClientErrorException {
        Player updatedPlayer = playerService
                .updatePlayerAndSaveInCache(fields);
        PlayerDTO updatedPlayerDTO = playerDTOMapper
                .apply(updatedPlayer);
        return new ResponseEntity<>(updatedPlayerDTO, HttpStatus.OK);
    }
    @DeleteMapping("/delete")
    public HttpStatus deleteUser(
            final @RequestParam String username)
            throws HttpClientErrorException {
        Player player = playerService.findPlayerEntityByUsername(username);
        gameReviewService.deleteAllReviewsByPlayer(player);
        playerService.deletePlayer(player);
        return HttpStatus.OK;
    }
    @GetMapping("/all_players_with_games")
    public List<PlayerWithGamesDTO> getAllPlayersWithGames() {
        List<Player> players = playerService.findAllPlayers();
        return players.stream()
                .map(playerWithGamesDTOMapper)
                .toList();
    }
    @GetMapping("/internal_server_error")
    public void internalServerErrorException() {
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
