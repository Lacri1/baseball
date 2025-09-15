package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.mapper.BatterMapper;
import com.baseball.game.mapper.PitcherMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.*;

class GameServiceImplTest {

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameLifecycleService lifecycleService;
    @Mock
    private GameStateService stateService;
    @Mock
    private GameActionService actionService;
    @Mock
    private GameValidationService validationService;
    @Mock
    private BatterMapper batterMapper;
    @Mock
    private PitcherMapper pitcherMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("createGame은 lifecycleService.createGame을 위임한다")
    void createGame_delegatesToLifecycleService() {
        // given
        GameDto dto = new GameDto();
        given(lifecycleService.createGame("A", "B", 9, true)).willReturn(dto);

        // when
        GameDto result = gameService.createGame("A", "B", 9, true);

        // then
        assertThat(result).isSameAs(dto);
        verify(lifecycleService).createGame("A", "B", 9, true);
    }

    @Test
    @DisplayName("getGame은 lifecycleService.getGame을 위임한다")
    void getGame_delegatesToLifecycleService() {
        GameDto dto = new GameDto();
        given(lifecycleService.getGame("id")).willReturn(dto);

        GameDto result = gameService.getGame("id");

        assertThat(result).isSameAs(dto);
        verify(lifecycleService).getGame("id");
    }

    @Test
    @DisplayName("batterSwing은 actionService.batterSwing을 위임한다")
    void batterSwing_delegatesToActionService() {
        given(actionService.batterSwing("id", true, true)).willReturn("result");

        String result = gameService.batterSwing("id", true, true);

        assertThat(result).isEqualTo("result");
        verify(actionService).batterSwing("id", true, true);
    }

    @Test
    @DisplayName("pitcherThrow는 actionService.pitcherThrow를 위임한다")
    void pitcherThrow_delegatesToActionService() {
        given(actionService.pitcherThrow("id", "Fastball")).willReturn("strike");

        String result = gameService.pitcherThrow("id", "Fastball");

        assertThat(result).isEqualTo("strike");
        verify(actionService).pitcherThrow("id", "Fastball");
    }

    @Test
    @DisplayName("playComputerTurn은 actionService.playComputerTurn을 위임한다")
    void playComputerTurn_delegatesToActionService() {
        given(actionService.playComputerTurn("id")).willReturn("AI");

        String result = gameService.playComputerTurn("id");

        assertThat(result).isEqualTo("AI");
        verify(actionService).playComputerTurn("id");
    }

    @Test
    @DisplayName("nextInning은 stateService.nextInning을 위임한다")
    void nextInning_delegatesToStateService() {
        GameDto dto = new GameDto();
        given(stateService.nextInning("id")).willReturn(dto);

        GameDto result = gameService.nextInning("id");

        assertThat(result).isSameAs(dto);
        verify(stateService).nextInning("id");
    }

    @Test
    @DisplayName("endGame은 stateService.endGame을 위임한다")
    void endGame_delegatesToStateService() {
        GameDto dto = new GameDto();
        given(stateService.endGame("id")).willReturn(dto);

        GameDto result = gameService.endGame("id");

        assertThat(result).isSameAs(dto);
        verify(stateService).endGame("id");
    }

    @Test
    @DisplayName("advanceRunners는 stateService.advanceRunners를 위임한다")
    void advanceRunners_delegatesToStateService() {
        gameService.advanceRunners("id", 2);

        verify(stateService).advanceRunners("id", 2);
    }

    @Test
    @DisplayName("resetGame은 lifecycleService.resetGame을 위임한다")
    void resetGame_delegatesToLifecycleService() {
        gameService.resetGame("id");

        verify(lifecycleService).resetGame("id");
    }
}