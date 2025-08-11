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
}
