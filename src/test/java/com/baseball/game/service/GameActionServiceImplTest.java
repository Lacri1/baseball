package com.baseball.game.service;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.exception.InvalidGameStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class GameActionServiceImplTest {
    @InjectMocks
    private GameActionServiceImpl service;
    @Mock
    private GameLifecycleService lifecycleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("batterSwing: 게임 종료 상태면 예외")
    void batterSwing_gameOver_throws() {
        GameDto game = new GameDto();
        game.setGameOver(true);
        given(lifecycleService.getGame("id")).willReturn(game);
        assertThatThrownBy(() -> service.batterSwing("id", true, 0.5))
                .isInstanceOf(InvalidGameStateException.class);
    }

    @Test
    @DisplayName("pitcherThrow: 정상 동작")
    void pitcherThrow_success() {
        GameDto game = new GameDto();
        game.setGameOver(false);
        Batter batter = new Batter();
        batter.setName("타자");
        Pitcher pitcher = new Pitcher();
        pitcher.setName("투수");
        game.setCurrentBatter(batter);
        game.setCurrentPitcher(pitcher);
        given(lifecycleService.getGame("id")).willReturn(game);
        String result = service.pitcherThrow("id", "Fastball");
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("playComputerTurn: 컴퓨터 턴이 아닌 경우")
    void playComputerTurn_notComputerTurn() {
        GameDto game = new GameDto();
        game.setUserOffense(true);
        game.setTop(true);
        given(lifecycleService.getGame("id")).willReturn(game);
        String result = service.playComputerTurn("id");
        assertThat(result).contains("유저의 턴");
    }
}
