package hvorostina.chesscomapi.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hvorostina.chesscomapi.chess_com_api.ChessComAPI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChessComServiceImplTest {

    String username;
    @Mock
    ChessComAPI api;
    @InjectMocks
    ChessComServiceImpl service;
    @BeforeEach
    void initialize() {
        username = "u_rich1";
    }
    @Test
    void getUserByUsername() throws JsonProcessingException {
        String str = "{\"username\":\"u_rich1\"}";
        ObjectMapper map = new ObjectMapper();
        JsonNode response = map.readTree(str);

        when(api.getUserByUsername(username)).thenReturn(response);

        JsonNode realResponse = service.getUserByUsername(username);

        assertEquals(response, realResponse);
    }
}