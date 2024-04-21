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
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameInDatabaseServiceImplTest {
    @Mock
    GameRepository gameRepository;
    @Mock
    GameDTOWithDateMapper gameDTOWithDateMapper;
    @Mock
    RequestGamesCacheServiceImpl gamesCacheService;
    @Mock
    PlayerRepository playerRepository;
    @InjectMocks
    GameInDatabaseServiceImpl gameService;

    private String testUuid;
    private Game testGame;
    private GameDTOWithDate testGameResponse;

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
        testGameResponse = GameDTOWithDate.builder()
                .gameURL((new URI(testURL)).toURL())
                .endGameTimeDate(testTime.atZone(ZoneId.of("Europe/Minsk")))
                .blackPlayer(new PlayerInGameDTO())
                .whitePlayer(new PlayerInGameDTO())
                .timeClass("blitz")
                .uuid(testUuid)
                .build();
    }

    @Test
    void whenGet_uuidExist_fromDatabase_thenGetGame(){
        when(gameRepository.findGameByUuid(testUuid))
                .thenReturn(Optional.of(testGame));
        when(gamesCacheService.getByUuid(testUuid)).thenReturn(null);
        when(gameDTOWithDateMapper.apply(testGame)).thenReturn(testGameResponse);

        GameDTOWithDate reallyFoundGameInDatabase = gameService.findGameByUUID(testUuid);

        assertEquals(testGameResponse, reallyFoundGameInDatabase);
    }
    @Test
    void whenGet_uuidExist_fromCache_thenGetGame(){

        when(gamesCacheService.getByUuid(testUuid)).thenReturn(testGameResponse);

        GameDTOWithDate reallyFoundGameInDatabase = gameService.findGameByUUID(testUuid);

        assertEquals(testGameResponse, reallyFoundGameInDatabase);
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
        when(gameDTOWithDateMapper.apply(any(Game.class))).thenReturn(testGameResponse);

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
        when(gameDTOWithDateMapper.apply(any(Game.class))).thenReturn(testGameResponse);

        Game reallyChangedGame = gameService.updateGameResult(fields);

        assertEquals(reallyChangedGame.getGameURL(), newTestURL);
        assertEquals(reallyChangedGame.getData(), newDateTime);
    }
}