package com.baseball.game.service;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.exception.InvalidGameStateException;
import com.baseball.game.exception.ValidationException;
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
    @Mock
    private GameStateService stateService;

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
        assertThatThrownBy(() -> service.batterSwing("id", true, true))
                .isInstanceOf(InvalidGameStateException.class);
    }

    @Test
    @DisplayName("pitcherThrow: 정상 동작")
    void pitcherThrow_success() {
        GameDto game = new GameDto();
        game.setGameOver(false);
        game.setTop(true); // 유저 수비 턴(컴퓨터 공격) -> 자동 컴퓨터 타격 허용
        Batter batter = new Batter();
        batter.setName("타자");
        Pitcher pitcher = new Pitcher();
        pitcher.setName("투수");
        game.setCurrentBatter(batter);
        game.setCurrentPitcher(pitcher);
        given(lifecycleService.getGame("id")).willReturn(game);
        String result = service.pitcherThrow("id", "strike");
        assertThat(result).isNotNull();
        then(stateService).should(org.mockito.Mockito.atLeastOnce()).checkCount("id");
    }

    @Test
    @DisplayName("pitcherThrow: 컴퓨터 공격 턴이면 응답에 자동 타격 결과가 덧붙는다")
    void pitcherThrow_computerOffense_appendsAutoBatting() {
        GameDto game = new GameDto();
        game.setGameOver(false);
        game.setUserOffense(false); // 유저 수비, 컴퓨터 공격
        game.setTop(true);

        Batter batter = new Batter();
        batter.setName("김재호");
        batter.setPlateAppearances(100);
        batter.setAtBats(80);
        batter.setHits(24);
        batter.setBattingAverage(0.3);

        Pitcher pitcher = new Pitcher();
        pitcher.setName("김광현");
        pitcher.setWhip(0.9); // 제구 양호 → intended strike일 때 스트라이크 확률 높음
        pitcher.setStrikeouts(150);
        pitcher.setPitchersBattersFaced(600);

        game.setCurrentBatter(batter);
        game.setCurrentPitcher(pitcher);

        given(lifecycleService.getGame("gid-app")).willReturn(game);

        String result = service.pitcherThrow("gid-app", "strike");

        assertThat(result).contains(" | 컴퓨터 타격: ");
    }

    @Test
    @DisplayName("pitcherThrow: 의도=ball, 제구 완벽(WHIP<=0.9) → 결과=볼")
    void pitcherThrow_intendedBall_perfectControl_returnsBall() {
        GameDto game = new GameDto();
        game.setGameOver(false);
        game.setTop(true); // 유저 수비 턴(컴퓨터 공격)
        Batter batter = new Batter();
        batter.setName("타자");
        batter.setPlateAppearances(1);
        Pitcher pitcher = new Pitcher();
        pitcher.setName("투수");
        pitcher.setWhip(0.9);
        game.setCurrentBatter(batter);
        game.setCurrentPitcher(pitcher);
        given(lifecycleService.getGame("gid")).willReturn(game);

        String result = service.pitcherThrow("gid", "ball");
        assertThat(result).startsWith("볼");
        then(stateService).should(org.mockito.Mockito.atLeastOnce()).checkCount("gid");
    }

    @Test
    @DisplayName("pitcherThrow: 의도=strike, 제구 완벽(WHIP<=0.9) → 결과=스트라이크")
    void pitcherThrow_intendedStrike_perfectControl_returnsStrike() {
        GameDto game = new GameDto();
        game.setGameOver(false);
        game.setTop(true); // 유저 수비 턴(컴퓨터 공격)
        Batter batter = new Batter();
        batter.setName("타자");
        batter.setPlateAppearances(1);
        Pitcher pitcher = new Pitcher();
        pitcher.setName("투수");
        pitcher.setWhip(0.9);
        game.setCurrentBatter(batter);
        game.setCurrentPitcher(pitcher);
        given(lifecycleService.getGame("gid2")).willReturn(game);

        String result = service.pitcherThrow("gid2", "strike");
        assertThat(result).startsWith("스트라이크");
        then(stateService).should(org.mockito.Mockito.atLeastOnce()).checkCount("gid2");
    }

    @Test
    @DisplayName("pitcherThrow: 잘못된 pitchType이면 ValidationException")
    void pitcherThrow_invalidPitchType_throws() {
        GameDto game = new GameDto();
        game.setGameOver(false);
        Batter batter = new Batter();
        Pitcher pitcher = new Pitcher();
        game.setCurrentBatter(batter);
        game.setCurrentPitcher(pitcher);
        given(lifecycleService.getGame("bad")).willReturn(game);

        assertThatThrownBy(() -> service.pitcherThrow("bad", "fastball"))
                .isInstanceOf(ValidationException.class);
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

    @Test
    @DisplayName("batterSwing: 현재 타자/투수 없으면 예외")
    void batterSwing_noParticipants_throws() {
        GameDto game = new GameDto();
        game.setGameOver(false);
        game.setCurrentBatter(null);
        game.setCurrentPitcher(null);
        given(lifecycleService.getGame("gid")).willReturn(game);
        assertThatThrownBy(() -> service.batterSwing("gid", true, true))
                .isInstanceOf(InvalidGameStateException.class);
    }
}
