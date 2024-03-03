package hvorostina.chesscomapi.controller;

import com.fasterxml.jackson.databind.JsonNode;
import hvorostina.chesscomapi.service.ChessComService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/chess-com")
public class ChessComController {
    private final ChessComService service;
    @GetMapping
    public JsonNode getUserByUsername(String username) {
        return service.getUserByUsername(username);
    }
}
