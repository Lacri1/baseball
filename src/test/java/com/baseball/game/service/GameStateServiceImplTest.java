package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.exception.InvalidGameStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class GameStateServiceImplTest {
    @InjectMocks
    private GameStateServiceImpl service;
    @Mock
    private GameLifecycleService lifecycleService;
    @Mock
    private GameValidationService validationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("nextInning: 이닝 전환 불가 시 예외")
    void nextInning_cannotChange_throws() {
        given(validationService.canChangeInning("id")).willReturn(false);
        GameDto game = new GameDto();
        given(lifecycleService.getGame("id")).willReturn(game);
        assertThatThrownBy(() -> service.nextInning("id"))
                .isInstanceOf(InvalidGameStateException.class);
    }

    @Test
    @DisplayName("endGame: 승자 결정")
    void endGame_winner() {
        GameDto game = new GameDto();
        game.setHomeScore(5);
        game.setAwayScore(3);
        given(lifecycleService.getGame("id")).willReturn(game);
        GameDto result = service.endGame("id");
        assertThat(result.isGameOver()).isTrue();
        assertThat(result.getWinner()).isEqualTo(game.getHomeTeam());
    }
}
