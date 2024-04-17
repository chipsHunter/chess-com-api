package hvorostina.chesscomapi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import hvorostina.chesscomapi.chess_com_api.ChessComAPI;
import hvorostina.chesscomapi.service.ChessComService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Service
@Primary
public class ChessComServiceImpl implements ChessComService {
    private final ChessComAPI api;
    @Override
    public JsonNode getUserByUsername(
            final @RequestParam String username) {
        return api.getUserByUsername(username);
    }
}
