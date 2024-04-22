package hvorostina.chesscomapi.service;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.GameReview;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTOWithDate;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import hvorostina.chesscomapi.model.mapper.GameDTOWithDateMapper;
import hvorostina.chesscomapi.model.mapper.PlayersInGameDTOMapper;
import hvorostina.chesscomapi.repository.GameRepository;
import hvorostina.chesscomapi.repository.GameReviewRepository;
import hvorostina.chesscomapi.service.impl.GameReviewInDatabaseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpServerErrorException;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameReviewInDatabaseServiceImplTest {

    @InjectMocks
    GameReviewInDatabaseServiceImpl  service;
    @Mock
    GameReviewRepository repository;
    GameDTOWithDateMapper gameMapper;
    @Mock
    GameRepository gameRepository;

    private GameReview testReviewForFirstPlayer;
    private GameReview testReviewForSecondPlayer;
    private Game firstGame;
    private GameDTOWithDate firstGameDTO;
    private GameDTOWithDate secondGameDTO;
    private Game secondGame;
    private Player firstPlayer;
    private Player secondPlayer;

    @BeforeEach
    public void initializeTestReview() throws URISyntaxException, MalformedURLException {
        gameMapper = new GameDTOWithDateMapper(new PlayersInGameDTOMapper());
        LocalDateTime testTime = LocalDateTime.now();
        firstPlayer = Player.builder()
                .id(1)
                .username("test1")
                .country("BY")
                .status("basic")
                .build();
        secondPlayer = Player.builder()
                .id(2)
                .username("test2")
                .country("BY")
                .status("basic")
                .build();
        firstGame = Game.builder()
                .id(1)
                .gameURL("https://www.chess.com/game/live/1")
                .uuid("1b-1b-1b")
                .gameResult("loss")
                .timeClass("blitz")
                .data(testTime)
                .players(List.of(firstPlayer, secondPlayer))
                .whiteRating(100)
                .blackRating(101)
                .winnerSide("white")
                .build();
        secondGame = Game.builder()
                .id(2)
                .gameURL("https://www.chess.com/game/live/2")
                .uuid("2b-2b-2b")
                .gameResult("checkmated")
                .timeClass("blitz")
                .data(testTime)
                .players(List.of(firstPlayer, secondPlayer))
                .whiteRating(150)
                .blackRating(140)
                .winnerSide("white")
                .build();

        testReviewForFirstPlayer = GameReview.builder()
                .id(1)
                .user(firstPlayer)
                .bestGame(secondGame)
                .winCasesRecord(1)
                .drawCasesRecord(0)
                .lossCasesRecord(0)
                .bestRating(150)
                .timeClass("blitz")
                .build();
        testReviewForSecondPlayer = GameReview.builder()
                .id(1)
                .user(secondPlayer)
                .bestGame(secondGame)
                .drawCasesRecord(0)
                .lossCasesRecord(1)
                .winCasesRecord(0)
                .bestRating(140)
                .timeClass("blitz")
                .build();
    }

    @Test
    void whenViewPlayerStatistics_PlayerHasGames_thenReturnReviewList() {
        firstPlayer.setGames(List.of(firstGame, secondGame));
        firstPlayer.setGameReviews(List.of(testReviewForFirstPlayer));
        List<GameReview> testPlayerStatistics = List.of(testReviewForFirstPlayer);

        List<GameReview> reallyGotStatistics = service.viewPlayerStatistics(firstPlayer);

        assertEquals(reallyGotStatistics, testPlayerStatistics);
    }

    @Test
    void whenViewPlayerStatistics_PlayerHasNoGame_thenReturnEmptyList() {
        List<GameReview> reallyGotStatistics = service.viewPlayerStatistics(secondPlayer);

        assertNull(reallyGotStatistics);
    }

    @Test
    void whenAddGame_NoReviewAndUuidNotExist_thenThrowInternalServerErrorException() {
        secondPlayer.setGames(List.of(secondGame));
        GameDTOWithDate addedGame = gameMapper.apply(secondGame);

        when(gameRepository.findGameByUuid(secondGame.getUuid())).thenReturn(Optional.empty());

        assertThrows(HttpServerErrorException.class, () ->
                service.manageGameReviewWhenAddGame(addedGame, secondPlayer));
    }
    @Test
    void whenAddGame_NoReviewAndUuidExist_thenCreateReview() {
        secondPlayer.setGames(List.of(secondGame));
        GameDTOWithDate addedGame = gameMapper.apply(secondGame);

        when(gameRepository.findGameByUuid(secondGame.getUuid())).thenReturn(Optional.of(secondGame));

        GameReview reallyCreatedReview = service.manageGameReviewWhenAddGame(addedGame, secondPlayer);

        assertEquals(reallyCreatedReview.getBestGame(), testReviewForSecondPlayer.getBestGame());
        assertEquals(reallyCreatedReview.getBestRating(), testReviewForSecondPlayer.getBestRating());
        assertEquals(reallyCreatedReview.getUser(), testReviewForSecondPlayer.getUser());
        assertEquals(reallyCreatedReview.getWinCasesRecord(), testReviewForSecondPlayer.getWinCasesRecord());
        assertEquals(reallyCreatedReview.getLossCasesRecord(), testReviewForSecondPlayer.getLossCasesRecord());
        assertEquals(reallyCreatedReview.getDrawCasesRecord(), testReviewForSecondPlayer.getDrawCasesRecord());
    }

    @Test
    void whenAddGame_ReviewExistAndUuidNotExist_thenThrowInternalServerError() {
        firstPlayer.setGames(List.of(firstGame, secondGame));
        firstPlayer.setGameReviews(List.of(testReviewForFirstPlayer));
        GameDTOWithDate addedGame = gameMapper.apply(firstGame);

        when(gameRepository.findGameByUuid(firstGame.getUuid())).thenReturn(Optional.empty());

        assertThrows(HttpServerErrorException.class, () ->
                service.manageGameReviewWhenAddGame(addedGame, firstPlayer));
    }
    @Test
    void whenAddGame_ReviewExistAndGameNotBest_thenUpdateReview() {
        firstPlayer.setGames(List.of(secondGame));
        firstPlayer.setGameReviews(List.of(testReviewForFirstPlayer));
        GameDTOWithDate addedGame = gameMapper.apply(firstGame);

        when(gameRepository.findGameByUuid(firstGame.getUuid())).thenReturn(Optional.of(firstGame));

        GameReview reallyCreatedReview = service.manageGameReviewWhenAddGame(addedGame, firstPlayer);

        assertEquals(reallyCreatedReview.getBestGame(), testReviewForFirstPlayer.getBestGame());
        assertEquals(reallyCreatedReview.getBestRating(), testReviewForFirstPlayer.getBestRating());
        assertEquals(reallyCreatedReview.getUser(), testReviewForFirstPlayer.getUser());
        assertEquals(reallyCreatedReview.getWinCasesRecord(), testReviewForFirstPlayer.getWinCasesRecord());
        assertEquals(reallyCreatedReview.getLossCasesRecord(), testReviewForFirstPlayer.getLossCasesRecord());
        assertEquals(reallyCreatedReview.getDrawCasesRecord(), testReviewForFirstPlayer.getDrawCasesRecord());
    }
    @Test
    void whenAddGame_ReviewExistAndGameBest_thenUpdateReview() {
        firstPlayer.setGames(List.of(firstGame));
        testReviewForFirstPlayer.setBestRating(100);
        testReviewForFirstPlayer.setBestGame(firstGame);
        firstPlayer.setGameReviews(List.of(testReviewForFirstPlayer));
        GameDTOWithDate addedGame = gameMapper.apply(secondGame);

        when(gameRepository.findGameByUuid(secondGame.getUuid())).thenReturn(Optional.of(secondGame));

        GameReview reallyCreatedReview = service.manageGameReviewWhenAddGame(addedGame, firstPlayer);

        assertEquals(reallyCreatedReview.getBestGame(), testReviewForFirstPlayer.getBestGame());
        assertEquals(reallyCreatedReview.getBestRating(), testReviewForFirstPlayer.getBestRating());
        assertEquals(reallyCreatedReview.getUser(), testReviewForFirstPlayer.getUser());
        assertEquals(reallyCreatedReview.getWinCasesRecord(), testReviewForFirstPlayer.getWinCasesRecord());
        assertEquals(reallyCreatedReview.getLossCasesRecord(), testReviewForFirstPlayer.getLossCasesRecord());
        assertEquals(reallyCreatedReview.getDrawCasesRecord(), testReviewForFirstPlayer.getDrawCasesRecord());
    }

    @Test
    void whenDeleteGame_reviewNotExist_thenThrowInternalServerError() {
        firstPlayer.setGames(List.of(firstGame));
        GameDTOWithDate addedGame = gameMapper.apply(secondGame);

        assertThrows(HttpServerErrorException.class, () ->
                service.manageGameReviewWhenDeleteGame(addedGame, firstPlayer));
    }
/*
    @Test
    void whenDeleteGame_lastInTimeClass_thenDeleteReview() {
        firstPlayer.setGames(List.of(secondGame));
        firstPlayer.setGameReviews(List.of(testReviewForFirstPlayer));
        GameDTOWithDate addedGame = gameMapper.apply(secondGame);

        service.manageGameReviewWhenDeleteGame(addedGame, firstPlayer);

        verify(gameRepository, times(1)).delete(secondGame);
    }

    @Test
    void deleteAllReviewsByPlayer() {
    }

    @Test
    void deleteAllReviews() {
    }*/
}