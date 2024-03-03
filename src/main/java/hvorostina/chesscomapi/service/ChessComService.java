package hvorostina.chesscomapi.service;

import com.fasterxml.jackson.databind.JsonNode;

public interface ChessComService {

    JsonNode getUserByUsername(String username);
}
