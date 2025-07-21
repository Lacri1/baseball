package com.baseball.game.controller;

import com.baseball.game.dto.GameCreateRequest;
import com.baseball.game.dto.GameDto;
import com.baseball.game.service.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    private GameDto testGame;

    @BeforeEach
    void setUp() {
        testGame = new GameDto();
        testGame.setGameId("test-game-id");
        testGame.setHomeTeam("삼성");
        testGame.setAwayTeam("두산");
        testGame.setInning(1);
        testGame.setTop(true);
        testGame.setHomeScore(0);
        testGame.setAwayScore(0);
        testGame.setGameOver(false);
    }

    @Test
    @DisplayName("게임 생성 API 테스트")
    void testCreateGame() throws Exception {
        // given
        GameCreateRequest request = new GameCreateRequest();
        request.setHomeTeam("삼성");
        request.setAwayTeam("두산");
        request.setMaxInning(9);
        request.setIsUserOffense(true);

        when(gameService.createGame(any(String.class), any(String.class)))
                .thenReturn(testGame);

        // when & then
        mockMvc.perform(post("/api/baseball/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("test-game-id"))
                .andExpect(jsonPath("$.homeTeam").value("삼성"))
                .andExpect(jsonPath("$.awayTeam").value("두산"));
    }

    @Test
    @DisplayName("같은 팀으로 게임 생성 시 400 에러")
    void testCreateGameWithSameTeam() throws Exception {
        // given
        GameCreateRequest request = new GameCreateRequest();
        request.setHomeTeam("삼성");
        request.setAwayTeam("삼성");
        request.setMaxInning(9);

        // when & then
        mockMvc.perform(post("/api/baseball/game")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("게임 조회 API 테스트")
    void testGetGame() throws Exception {
        // given
        when(gameService.getGame("test-game-id")).thenReturn(testGame);

        // when & then
        mockMvc.perform(get("/api/baseball/game/test-game-id"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.game.gameId").value("test-game-id"));
    }

    @Test
    @DisplayName("존재하지 않는 게임 조회 시 404 에러")
    void testGetGameNotFound() throws Exception {
        // given
        when(gameService.getGame("non-existent-id"))
                .thenThrow(new com.baseball.game.exception.GameNotFoundException("non-existent-id"));

        // when & then
        mockMvc.perform(get("/api/baseball/game/non-existent-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("GAME_NOT_FOUND"));
    }

    @Test
    @DisplayName("타격 API 테스트")
    void testBatterSwing() throws Exception {
        // given
        when(gameService.batterSwing(any(String.class), any(Boolean.class), any(Double.class)))
                .thenReturn("안타");
        when(gameService.getGame("test-game-id")).thenReturn(testGame);

        // when & then
        mockMvc.perform(post("/api/baseball/game/test-game-id/batter")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"swing\": true, \"timing\": 0.5}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value("안타"));
    }

    @Test
    @DisplayName("타격 API - 스윙 정보 누락 시 400 에러")
    void testBatterSwingMissingSwing() throws Exception {
        // when & then
        mockMvc.perform(post("/api/baseball/game/test-game-id/batter")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"timing\": 0.5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("투구 API 테스트")
    void testPitcherThrow() throws Exception {
        // given
        when(gameService.pitcherThrow(any(String.class), any(String.class)))
                .thenReturn("스트라이크");
        when(gameService.getGame("test-game-id")).thenReturn(testGame);

        // when & then
        mockMvc.perform(post("/api/baseball/game/test-game-id/pitcher")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"pitchType\": \"strike\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").value("스트라이크"));
    }

    @Test
    @DisplayName("이닝 진행 API 테스트")
    void testNextInning() throws Exception {
        // given
        GameDto nextInningGame = new GameDto();
        nextInningGame.setGameId("test-game-id");
        nextInningGame.setInning(2);
        nextInningGame.setTop(true);

        when(gameService.nextInning("test-game-id")).thenReturn(nextInningGame);

        // when & then
        mockMvc.perform(post("/api/baseball/game/test-game-id/next-inning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.game.inning").value(2));
    }

    @Test
    @DisplayName("게임 종료 API 테스트")
    void testEndGame() throws Exception {
        // given
        GameDto endedGame = new GameDto();
        endedGame.setGameId("test-game-id");
        endedGame.setGameOver(true);
        endedGame.setWinner("삼성");

        when(gameService.endGame("test-game-id")).thenReturn(endedGame);

        // when & then
        mockMvc.perform(post("/api/baseball/game/test-game-id/end"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.game.gameOver").value(true))
                .andExpect(jsonPath("$.game.winner").value("삼성"));
    }

    @Test
    @DisplayName("베이스 러닝 API 테스트")
    void testAdvanceRunners() throws Exception {
        // given
        when(gameService.getGame("test-game-id")).thenReturn(testGame);

        // when & then
        mockMvc.perform(post("/api/baseball/game/test-game-id/advance-runners")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"bases\": 2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("게임 통계 조회 API 테스트")
    void testGetGameStats() throws Exception {
        // given
        when(gameService.getGameStats("test-game-id"))
                .thenReturn("=== 게임 통계 ===\n홈팀: 삼성 (0점)\n원정팀: 두산 (0점)");

        // when & then
        mockMvc.perform(get("/api/baseball/game/test-game-id/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.stats").value("=== 게임 통계 ===\n홈팀: 삼성 (0점)\n원정팀: 두산 (0점)"));
    }
}