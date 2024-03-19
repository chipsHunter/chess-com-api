package hvorostina.chesscomapi.service.impl;

import hvorostina.chesscomapi.model.Game;
import hvorostina.chesscomapi.model.Player;
import hvorostina.chesscomapi.model.dto.GameDTO;
import hvorostina.chesscomapi.model.dto.PlayerInGameDTO;
import hvorostina.chesscomapi.model.mapper.GameDTOMapper;
import hvorostina.chesscomapi.repository.GameRepository;
import hvorostina.chesscomapi.repository.GameReviewRepository;
import hvorostina.chesscomapi.repository.PlayerRepository;
import hvorostina.chesscomapi.service.GameService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@lombok.Data
@Service
@Transactional
public class GameInDatabaseServiceImpl implements GameService {
    private final GameDTOMapper gameDTOMapper;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final GameReviewRepository gameReviewRepository;
    @Override
    public Optional<GameDTO> addGame(GameDTO game) {
        if(gameRepository.findGameByUUID(game.getUuid()).isPresent())
            return Optional.empty();
        Game newGame = new Game();
        newGame.setGameURL(game.getGameURL().toString());
        newGame.setData(game.getGameTimestamp());
        newGame.setUuid(game.getUuid());
        List<Player> players = new ArrayList<>();
        Optional<Player> whitePlayer = playerRepository
                .findPlayerByUsername(game
                        .getWhitePlayer()
                        .getUsername());
        if(whitePlayer.isEmpty())
            return Optional.empty();
        players.add(0, whitePlayer.get());
        Optional<Player> blackPlayer = playerRepository
                .findPlayerByUsername(game
                        .getBlackPlayer()
                        .getUsername());
        if(blackPlayer.isEmpty())
            return Optional.empty();
        players.add(1, blackPlayer.get());
        newGame.setPlayers(players);
        newGame.setWhiteRating(game.getWhitePlayer().getRating());
        newGame.setBlackRating(game.getBlackPlayer().getRating());
        if(Objects.equals(game.getWhitePlayer().getGameResult(), "win")) {
            newGame.setWinnerSide("white");
            newGame.setGameResult(game.getBlackPlayer().getGameResult());
        }
        else {
            newGame.setWinnerSide("black");
            newGame.setGameResult(game.getWhitePlayer().getGameResult());
        }
        newGame.setTimeClass(game.getTimeClass());
        return Optional.of(gameDTOMapper.apply(gameRepository.save(newGame)));
    }

    @Override
    public Optional<GameDTO> updateGameResult(GameDTO gameParams) {
        Optional<Game> updatedGame =  gameRepository.findGameByUUID(gameParams.getUuid());
        if(updatedGame.isEmpty())
            return Optional.empty();
        if(gameParams.getGameTimestamp() != null)
            updatedGame.get().setData(gameParams.getGameTimestamp());
        if(gameParams.getTimeClass() != null)
            updatedGame.get().setTimeClass(gameParams.getTimeClass());
        if(gameParams.getGameURL() != null)
            updatedGame.get().setGameURL(gameParams.getGameURL().toString());
        if(gameParams.getBlackPlayer() != null) {
            PlayerInGameDTO blackPlayer = gameParams.getBlackPlayer();
            Optional<Player> newBlackPlayer = playerRepository.findPlayerByUsername(blackPlayer.getUsername());
            if(newBlackPlayer.isEmpty())
                return Optional.empty();
            if(Objects.equals(blackPlayer.getGameResult(), "win")) {
                updatedGame.get().setWinnerSide("black");
                updatedGame.get().setGameResult(gameParams.getWhitePlayer().getGameResult());
            }
            updatedGame.get().setBlackRating(blackPlayer.getRating());
            updatedGame.get().getPlayers().set(1, newBlackPlayer.get());
        }
        if(gameParams.getWhitePlayer() != null) {
            PlayerInGameDTO whitePlayer = gameParams.getWhitePlayer();
            Optional<Player> newWhitePlayer = playerRepository.findPlayerByUsername(whitePlayer.getUsername());
            if(newWhitePlayer.isEmpty())
                return Optional.empty();
            if(Objects.equals(whitePlayer.getGameResult(), "win")) {
                updatedGame.get().setWinnerSide("white");
                updatedGame.get().setGameResult(gameParams.getBlackPlayer().getGameResult());
            }
            updatedGame.get().setWhiteRating(whitePlayer.getRating());
            updatedGame.get().getPlayers().set(1, newWhitePlayer.get());
        }
        return Optional.of(gameDTOMapper.apply(updatedGame.get()));
    }

    @Override
    public List<GameDTO> findAllGames() {
        return gameRepository.findAll()
                .stream().map(gameDTOMapper)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<GameDTO> findGameByUUID(String uuid) {
        return gameRepository
                .findGameByUUID(uuid)
                .map(gameDTOMapper);
    }

    @Override
    public void deleteGame(String uuid) {
        Optional<Game> game = gameRepository.findGameByUUID(uuid);
        if(game.isEmpty())
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        gameRepository.delete(game.get());
    }
}
