package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class GameValidationServiceImplTest {
    @InjectMocks
    private GameValidationServiceImpl service;
    @Mock
    private GameLifecycleService lifecycleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("isGamePlayable: 게임이 종료되었으면 false")
    void isGamePlayable_gameOver() {
        GameDto game = new GameDto();
        game.setGameOver(true);
        given(lifecycleService.getGame("id")).willReturn(game);
        boolean result = service.isGamePlayable("id");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("isValidGameState: null이면 false")
    void isValidGameState_null() {
        boolean result = service.isValidGameState(null);
        assertThat(result).isFalse();
    }
}
