package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.exception.GameNotFoundException;
import com.baseball.game.exception.InvalidGameStateException;
import com.baseball.game.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceImplTest {

    private GameServiceImpl gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameServiceImpl();
    }

    @Test
    @DisplayName("게임 생성 테스트")
    void testCreateGame() {
        // given
        String homeTeam = "삼성";
        String awayTeam = "두산";

        // when
        GameDto game = gameService.createGame(homeTeam, awayTeam);

        // then
        assertNotNull(game);
        assertEquals(homeTeam, game.getHomeTeam());
        assertEquals(awayTeam, game.getAwayTeam());
        assertNotNull(game.getGameId());
        assertEquals(1, game.getInning());
        assertTrue(game.isTop());
        assertEquals(0, game.getHomeScore());
        assertEquals(0, game.getAwayScore());
        assertFalse(game.isGameOver());
    }

    @Test
    @DisplayName("같은 팀으로 게임 생성 시 예외 발생")
    void testCreateGameWithSameTeam() {
        // given
        String team = "삼성";

        // when & then
        assertThrows(ValidationException.class, () -> {
            gameService.createGame(team, team);
        });
    }

    @Test
    @DisplayName("null 팀으로 게임 생성 시 예외 발생")
    void testCreateGameWithNullTeam() {
        // when & then
        assertThrows(ValidationException.class, () -> {
            gameService.createGame(null, "삼성");
        });

        assertThrows(ValidationException.class, () -> {
            gameService.createGame("삼성", null);
        });
    }

    @Test
    @DisplayName("게임 조회 테스트")
    void testGetGame() {
        // given
        GameDto createdGame = gameService.createGame("삼성", "두산");
        String gameId = createdGame.getGameId();

        // when
        GameDto foundGame = gameService.getGame(gameId);

        // then
        assertNotNull(foundGame);
        assertEquals(createdGame.getGameId(), foundGame.getGameId());
    }

    @Test
    @DisplayName("존재하지 않는 게임 조회 시 예외 발생")
    void testGetGameNotFound() {
        // when & then
        assertThrows(GameNotFoundException.class, () -> {
            gameService.getGame("non-existent-id");
        });
    }

    @Test
    @DisplayName("null 게임 ID로 조회 시 예외 발생")
    void testGetGameWithNullId() {
        // when & then
        assertThrows(ValidationException.class, () -> {
            gameService.getGame(null);
        });
    }

    @Test
    @DisplayName("타격 테스트")
    void testBatterSwing() {
        // given
        GameDto game = gameService.createGame("삼성", "두산");

        // when
        String result = gameService.batterSwing(game.getGameId(), true, 0.5);

        // then
        assertNotNull(result);
        assertTrue(result.contains("홈런") || result.contains("안타") ||
                result.contains("헛스윙") || result.contains("땅볼") ||
                result.contains("뜬공"));
    }

    @Test
    @DisplayName("종료된 게임에서 타격 시 예외 발생")
    void testBatterSwingOnEndedGame() {
        // given
        GameDto game = gameService.createGame("삼성", "두산");
        gameService.endGame(game.getGameId());

        // when & then
        assertThrows(InvalidGameStateException.class, () -> {
            gameService.batterSwing(game.getGameId(), true, 0.5);
        });
    }

    @Test
    @DisplayName("이닝 진행 테스트")
    void testNextInning() {
        // given
        GameDto game = gameService.createGame("삼성", "두산");
        int initialInning = game.getInning();

        // when
        GameDto nextInningGame = gameService.nextInning(game.getGameId());

        // then
        assertNotNull(nextInningGame);
        assertEquals(initialInning + 1, nextInningGame.getInning());
        assertEquals(0, nextInningGame.getOut());
        assertEquals(0, nextInningGame.getStrike());
        assertEquals(0, nextInningGame.getBall());
    }

    @Test
    @DisplayName("게임 종료 테스트")
    void testEndGame() {
        // given
        GameDto game = gameService.createGame("삼성", "두산");
        game.setHomeScore(5);
        game.setAwayScore(3);

        // when
        GameDto endedGame = gameService.endGame(game.getGameId());

        // then
        assertTrue(endedGame.isGameOver());
        assertEquals("삼성", endedGame.getWinner());
    }

    @Test
    @DisplayName("무승부 게임 종료 테스트")
    void testEndGameTie() {
        // given
        GameDto game = gameService.createGame("삼성", "두산");
        game.setHomeScore(3);
        game.setAwayScore(3);

        // when
        GameDto endedGame = gameService.endGame(game.getGameId());

        // then
        assertTrue(endedGame.isGameOver());
        assertEquals("무승부", endedGame.getWinner());
    }

    @Test
    @DisplayName("베이스 러닝 테스트")
    void testAdvanceRunners() {
        // given
        GameDto game = gameService.createGame("삼성", "두산");
        Batter runner = new Batter();
        runner.setName("테스트러너");
        game.getBases()[1] = runner; // 1루에 주자 배치

        // when
        gameService.advanceRunners(game.getGameId(), 2);

        // then
        assertNull(game.getBases()[1]); // 1루 비어있음
        assertNotNull(game.getBases()[3]); // 3루에 주자 있음
    }

    @Test
    @DisplayName("게임 통계 조회 테스트")
    void testGetGameStats() {
        // given
        GameDto game = gameService.createGame("삼성", "두산");
        game.setHomeScore(5);
        game.setAwayScore(3);

        // when
        String stats = gameService.getGameStats(game.getGameId());

        // then
        assertNotNull(stats);
        assertTrue(stats.contains("삼성"));
        assertTrue(stats.contains("두산"));
        assertTrue(stats.contains("5"));
        assertTrue(stats.contains("3"));
    }
}