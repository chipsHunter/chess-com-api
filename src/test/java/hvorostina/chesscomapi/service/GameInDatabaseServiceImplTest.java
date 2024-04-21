package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.in_memory_cache.RequestGamesCacheServiceImpl;
import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import hvorostina.chesscomapi.model.mapper.GameDTOWithDateMapper;
import hvorostina.chesscomapi.repository.GameRepository;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.impl.GameInDatabaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameInDatabaseServiceImplTest {
    @Mock
    GameRepository gameRepository;
    @Mock
    RequestGamesCacheServiceImpl gamesCacheService;
    @Mock
    PlayerRepository playerRepository;
    @InjectMocks
    GameInDatabaseServiceImpl gameService;

    private String testUuid;
    private Game testGame;

    @BeforeEach
    void initializeTestGame() throws URISyntaxException, MalformedURLException {
        LocalDateTime testTime = LocalDateTime.now();
        String testURL = "https://www.chess.com/game/live/1";
        testUuid = "1b-1b-1b";
        testGame = Game.builder()
                .id(1)
                .gameURL(testURL)
                .timeClass("blitz")
                .gameResult("checkmated")
                .players(List.of())
                .uuid(testUuid)
                .whiteRating(100)
                .blackRating(200)
                .data(testTime)
                .winnerSide("black")
                .build();
    }

    @Test
    void whenAdd_GameExist_thenSaveNothing() {
        when(gameRepository.findGameByUuid(testUuid)).thenReturn(Optional.of(testGame));

        gameService.addGame(testGame);

        verify(gameRepository, times(0)).save(any(Game.class));
    }

    @Test
    void whenAdd_GameNotExist_thenAddItToDatabase() {
        when(gameRepository.findGameByUuid(testUuid)).thenReturn(Optional.empty());

        gameService.addGame(testGame);

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void whenGet_uuidExist_fromDatabase_thenGetGame() {
        when(gameRepository.findGameByUuid(testUuid))
                .thenReturn(Optional.of(testGame));
        when(gamesCacheService.getByUuid(testUuid)).thenReturn(null);

        Game reallyFoundGameInDatabase = gameService.findGameByUUID(testUuid);

        assertEquals(testGame, reallyFoundGameInDatabase);
    }
    @Test
    void whenGet_uuidExist_fromCache_thenGetGame(){

        when(gamesCacheService.getByUuid(testUuid)).thenReturn(testGame);

        Game reallyFoundGameInDatabase = gameService.findGameByUUID(testUuid);

        assertEquals(testGame, reallyFoundGameInDatabase);
    }
    @Test
    void whenGet_notExistsUuid_thenThrowNotFoundException(){

        when(gameRepository.findGameByUuid(testUuid)).thenReturn(Optional.empty());
        when(gamesCacheService.getByUuid(testUuid)).thenReturn(null);

        assertThrows(HttpClientErrorException.class, () ->
                gameService.findGameByUUID(testUuid) );
    }

    @Test
    void updateGameResult_uuidNotExist_thenThrowNotFound() {
        GameDTO fields = GameDTO.builder()
                .uuid(testUuid)
                .build();

        when(gameRepository.findGameByUuid(testUuid))
                .thenReturn(Optional.empty());

        assertThrows(HttpClientErrorException.class, () ->
                gameService.updateGameResult(fields));

    }

    @Test
    void getAllGames_PlayerNotExist_thenThrowNotFound() {
        String testUsername = "test";

        when(playerRepository.findPlayerByUsername(testUsername))
                .thenReturn(Optional.empty());

        assertThrows(HttpClientErrorException.class, () ->
                gameService.findAllGamesByUsername(testUsername));
    }

    @Test
    void getAllGames_PlayerExist_thenThrowNotFound() {
        String testUsername = "test";
        Player testPlayer = new Player();
        List<Game> testUserGames = List.of(testGame);
        testPlayer.setGames(testUserGames);

        when(playerRepository.findPlayerByUsername(testUsername))
                .thenReturn(Optional.of(testPlayer));

        List<Game> realUserGames = gameService.findAllGamesByUsername(testUsername);

        assertEquals(realUserGames, testUserGames);
    }

    @Test
    void updateGame_NoFieldSpecified_thenChangeNothing()  {
        GameDTO fields = GameDTO.builder()
                .uuid(testUuid)
                .build();
        when(gameRepository.findGameByUuid(testUuid)).thenReturn(Optional.of(testGame));

        Game reallyChangedGame = gameService.updateGameResult(fields);

        assertEquals(reallyChangedGame.getGameURL(), testGame.getGameURL());
        assertEquals(reallyChangedGame.getData(), testGame.getData());
    }
    @Test
    void updateGame_FieldsSpecified_thenChangeGame()  {
        String newTestURL = "https://www.chess.com/game/live/2";
        long newTestTimestamp = 1670229381L;
        Instant timeInstant = Instant.ofEpochSecond(newTestTimestamp);
        LocalDateTime newDateTime = LocalDateTime
                .ofInstant(timeInstant, ZoneId.of("Europe/Minsk"));
        GameDTO fields = GameDTO.builder()
                .uuid(testUuid)
                .gameURL(newTestURL)
                .gameTimestamp(newTestTimestamp)
                .build();
        when(gameRepository.findGameByUuid(testUuid)).thenReturn(Optional.of(testGame));

        Game reallyChangedGame = gameService.updateGameResult(fields);

        assertEquals(reallyChangedGame.getGameURL(), newTestURL);
        assertEquals(reallyChangedGame.getData(), newDateTime);
    }
    @Test
    void getUserGamesBetweenDates_userWithIdExist_thenReturnTheirGames() {
        int testPlayerId = 1;
        List<Game> testListOfPlayerGames = List.of(testGame);
        LocalDateTime firstDate = LocalDateTime.of(2005, Month.MAY, 24, 11, 0);
        LocalDateTime secondDate = LocalDateTime.of(2005, Month.MAY, 28, 17, 50);
        when(gameRepository.findGamesByPlayerInPeriod(testPlayerId, firstDate, secondDate)).thenReturn(testListOfPlayerGames);

        List<Game> reallyReturnedList = gameService.findGamesByUserBetweenDates(testPlayerId, firstDate, secondDate);

        assertEquals(testListOfPlayerGames, reallyReturnedList);
    }
    @Test
    void getUserGamesBetweenDates_userWithIdNotExist_thenReturnNothing() {
        int testPlayerId = 1;
        LocalDateTime firstDate = LocalDateTime.of(2005, Month.MAY, 24, 11, 0);
        LocalDateTime secondDate = LocalDateTime.of(2005, Month.MAY, 28, 17, 50);
        when(gameRepository.findGamesByPlayerInPeriod(testPlayerId, firstDate, secondDate)).thenReturn(List.of());

        List<Game> reallyReturnedList = gameService.findGamesByUserBetweenDates(testPlayerId, firstDate, secondDate);

        assertEquals(reallyReturnedList.size(), 0);
    }
    @Test
    void deleteGame_UuidExist_thenDeleteGame() {
        when(gameRepository.findGameByUuid(testUuid)).thenReturn(Optional.of(testGame));

        gameService.deleteGame(testUuid);

        verify(gamesCacheService, times(1)).deleteByUuid(testUuid);
        verify(gameRepository, times(1)).delete(testGame);
    }
    @Test
    void deleteGame_UuidNotExist_thenThrowBadRequest() {
        when(gameRepository.findGameByUuid(testUuid)).thenReturn(Optional.empty());

        assertThrows(HttpClientErrorException.class, () ->
                gameService.deleteGame(testUuid));
    }
}