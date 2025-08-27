package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class GameLifecycleServiceImplTest {
    private GameLifecycleServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new GameLifecycleServiceImpl();
    }

    @Test
    @DisplayName("createGame: 정상 생성")
    void createGame_success() {
        GameDto game = service.createGame("A", "B", 9, true);
        assertThat(game).isNotNull();
        assertThat(game.getGameId()).isNotNull();
        assertThat(service.existsGame(game.getGameId())).isTrue();
    }

    @Test
    @DisplayName("createGame: 홈/원정팀 같으면 예외")
    void createGame_sameTeam_throws() {
        assertThatThrownBy(() -> service.createGame("A", "A", 9, true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("resetGame: 정상 리셋")
    void resetGame_success() {
        GameDto game = service.createGame("A", "B", 9, true);
        service.resetGame(game.getGameId());
        GameDto reset = service.getGame(game.getGameId());
        assertThat(reset.getInning()).isEqualTo(1);
        assertThat(reset.getOut()).isEqualTo(0);
    }

    @Test
    @DisplayName("다음 이닝 전환: 초→말, 말→초+이닝증가")
    void nextInning_toggleAndIncrement() {
        GameStateServiceImpl state = new GameStateServiceImpl();
        // 수동 주입
        java.lang.reflect.Field f = null;
        try {
            f = GameStateServiceImpl.class.getDeclaredField("lifecycleService");
            f.setAccessible(true);
            f.set(state, service);
            f = GameStateServiceImpl.class.getDeclaredField("validationService");
            f.setAccessible(true);
            GameValidationService mockValidate = org.mockito.Mockito.mock(GameValidationService.class);
            org.mockito.BDDMockito.given(mockValidate.canChangeInning(org.mockito.ArgumentMatchers.anyString()))
                    .willReturn(true);
            f.set(state, mockValidate);
        } catch (Exception ignored) {
        }

        GameDto game = service.createGame("H", "A", 9, true);
        game.setTop(true);

        GameDto afterTop = state.nextInning(game.getGameId());
        org.assertj.core.api.Assertions.assertThat(afterTop.isTop()).isFalse();

        GameDto afterBottom = state.nextInning(game.getGameId());
        org.assertj.core.api.Assertions.assertThat(afterBottom.isTop()).isTrue();
        org.assertj.core.api.Assertions.assertThat(afterBottom.getInning()).isEqualTo(2);
    }
}
