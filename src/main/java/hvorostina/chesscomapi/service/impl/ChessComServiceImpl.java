package hvorostina.chesscomapi.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import hvorostina.chesscomapi.ChessComAPI.ChessComAPI;
import hvorostina.chesscomapi.service.ChessComService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ChessComServiceImpl implements ChessComService {
    private final ChessComAPI api;
    @Override
    public JsonNode getUserByUsername(String username) {
        return api.getUserByUsername(username);
    }
}
