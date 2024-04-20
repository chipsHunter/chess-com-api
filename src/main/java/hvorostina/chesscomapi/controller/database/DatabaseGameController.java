package hvorostina.chesscomapi.controller.database;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import hvorostina.chesscomapi.model.dto.UserGamesInPeriodRequestDTO;
import hvorostina.chesscomapi.model.mapper.GameMapper;
import hvorostina.chesscomapi.service.GameReviewService;
import hvorostina.chesscomapi.service.GameService;
import hvorostina.chesscomapi.service.PlayerService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/database/game")
public class DatabaseGameController {
    private final GameService gameService;
    private final GameReviewService gameReviewService;
    private final PlayerService playerService;
    private final GameMapper gameMapper;
    @PostMapping("/add")
    public ResponseEntity<GameDTOWithDate> addGame(
            final @RequestBody GameDTO gameDTO) {
        Game gameToAdd = gameMapper.apply(gameDTO);
        GameDTOWithDate addedGame = gameService.addGame(gameToAdd);
        String whitePlayerUsername = addedGame.getWhitePlayer().getUsername();
        Player whitePlayer = playerService
                .findPlayerEntityByUsername(whitePlayerUsername);
        String blackPlayerUsername = addedGame
                .getBlackPlayer().getUsername();
        Player blackPlayer = playerService
                .findPlayerEntityByUsername(blackPlayerUsername);
        gameReviewService.manageGameReviewWhenAddGame(addedGame, whitePlayer);
        gameReviewService.manageGameReviewWhenAddGame(addedGame, blackPlayer);
        return new ResponseEntity<>(addedGame, HttpStatus.CREATED);
    }
    @GetMapping("/find")
    public ResponseEntity<GameDTOWithDate> findGameByUUID(
            final @RequestParam String uuid) {
        GameDTOWithDate foundGame = gameService.findGameByUUID(uuid);
        return new ResponseEntity<>(foundGame, HttpStatus.OK);
    }
    @GetMapping("/find_in_period")
    public List<GameDTOWithDate> findAllPlayerGamesInPeriod(
            final @RequestBody UserGamesInPeriodRequestDTO requestDTO) {
        int playerID = playerService.getIdByUsername(requestDTO.getUsername());
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime start = LocalDateTime
                .parse(requestDTO.getStartData(), formatter);
        LocalDateTime end = LocalDateTime
                .parse(requestDTO.getEndData(), formatter);
        return gameService.findGamesByUserBetweenDates(playerID, start, end);
    }
    @PatchMapping("/update")
    public ResponseEntity<GameDTOWithDate> updateGame(
            final @RequestBody GameDTO gameDTO) {
        GameDTOWithDate updateGame = gameService.updateGameResult(gameDTO);
        return new ResponseEntity<>(updateGame, HttpStatus.OK);
    }
    @GetMapping("/find_all")
    public List<GameDTOWithDate> findAllGames(
            final @RequestParam String username) {
        return gameService.findAllGamesByUsername(username.toLowerCase());
    }
    @DeleteMapping("/delete")
    public HttpStatusCode deleteGame(
            final @RequestParam String uuid) {
        GameDTOWithDate gameDTO = gameService.findGameByUUID(uuid);
        String whitePlayerUsername = gameDTO.getWhitePlayer().getUsername();
        Player whitePlayer = playerService
                .findPlayerEntityByUsername(whitePlayerUsername);
        String blackPlayerUsername = gameDTO.getBlackPlayer().getUsername();
        Player blackPlayer = playerService
                .findPlayerEntityByUsername(blackPlayerUsername);
        if (whitePlayer == null || blackPlayer == null) {
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        gameReviewService.manageGameReviewWhenDeleteGame(gameDTO, whitePlayer);
        gameReviewService.manageGameReviewWhenDeleteGame(gameDTO, blackPlayer);
        gameService.deleteGame(uuid);
        return HttpStatus.OK;
    }
    @DeleteMapping("/delete_all")
    public HttpStatus deleteAllGames() {
        gameReviewService.deleteAllReviews();
        gameService.deleteAllGames();
        return HttpStatus.OK;
    }
}
