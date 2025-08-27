package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.exception.InvalidGameStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.baseball.game.mapper.MemberMapper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class GameStateServiceImplTest {
    @InjectMocks
    private GameStateServiceImpl service;
    @Mock
    private GameLifecycleService lifecycleService;
    @Mock
    private GameValidationService validationService;
    @Mock
    private MemberMapper memberMapper;

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

    @Test
    @DisplayName("checkCount: 삼진 임계 도달 시 아웃 증가 및 카운트 리셋, 타순 전진")
    void checkCount_strikeoutThreshold() {
        GameDto game = new GameDto();
        game.setStrike(3); // MAX_STRIKES 가정치 3
        game.setBall(1);
        game.setTop(true);
        // 공격팀 타순 준비(awayBattingOrder 사용됨)
        var batterA = new com.baseball.game.dto.Batter();
        batterA.setName("A");
        var batterB = new com.baseball.game.dto.Batter();
        batterB.setName("B");
        game.setAwayBattingOrder(java.util.Arrays.asList(batterA, batterB));
        game.setCurrentBatterIndex(0);
        game.setCurrentBatter(batterA);

        given(lifecycleService.getGame("gid")).willReturn(game);

        service.checkCount("gid");

        assertThat(game.getOut()).isEqualTo(1);
        assertThat(game.getStrike()).isZero();
        assertThat(game.getBall()).isZero();
        assertThat(game.getCurrentBatter()).isNotNull();
    }

    @Test
    @DisplayName("checkCount: 볼넷 임계 도달 시 진루 처리 및 카운트 리셋, 타순 전진")
    void checkCount_walkThreshold() {
        GameDto game = new GameDto();
        game.setBall(4); // MAX_BALLS 가정치 4
        game.setStrike(2);
        game.setTop(true);
        var batterA = new com.baseball.game.dto.Batter();
        batterA.setName("A");
        var batterB = new com.baseball.game.dto.Batter();
        batterB.setName("B");
        game.setAwayBattingOrder(java.util.Arrays.asList(batterA, batterB));
        game.setCurrentBatterIndex(0);
        game.setCurrentBatter(batterA);

        given(lifecycleService.getGame("gid2")).willReturn(game);

        service.checkCount("gid2");

        assertThat(game.getStrike()).isZero();
        assertThat(game.getBall()).isZero();
        assertThat(game.getCurrentBatter()).isNotNull();
    }

    @Test
    @DisplayName("endGame: 사용자 승리 시 멤버 집계 업데이트")
    void endGame_updatesMemberCounts_win() {
        GameDto game = new GameDto();
        game.setHomeTeam("H");
        game.setAwayTeam("A");
        game.setHomeScore(3);
        game.setAwayScore(1);
        game.setUserOffense(false); // 사용자=홈
        game.setUserId("u1");

        given(lifecycleService.getGame("id")).willReturn(game);

        service.endGame("id");

        then(memberMapper).should().incrementGame("u1");
        then(memberMapper).should().incrementWin("u1");
        then(memberMapper).should(never()).incrementLose("u1");
        then(memberMapper).should(never()).incrementDraw("u1");
    }

    @Test
    @DisplayName("endGame: 사용자 패배 시 멤버 집계 업데이트")
    void endGame_updatesMemberCounts_lose() {
        GameDto game = new GameDto();
        game.setHomeTeam("H");
        game.setAwayTeam("A");
        game.setHomeScore(1);
        game.setAwayScore(3);
        game.setUserOffense(false); // 사용자=홈
        game.setUserId("u1");

        given(lifecycleService.getGame("id")).willReturn(game);

        service.endGame("id");

        then(memberMapper).should().incrementGame("u1");
        then(memberMapper).should().incrementLose("u1");
        then(memberMapper).should(never()).incrementWin("u1");
        then(memberMapper).should(never()).incrementDraw("u1");
    }

    @Test
    @DisplayName("endGame: 무승부 시 멤버 집계 업데이트")
    void endGame_updatesMemberCounts_draw() {
        GameDto game = new GameDto();
        game.setHomeTeam("H");
        game.setAwayTeam("A");
        game.setHomeScore(2);
        game.setAwayScore(2);
        game.setUserOffense(true); // 사용자=원정
        game.setUserId("u1");

        given(lifecycleService.getGame("id")).willReturn(game);

        service.endGame("id");

        then(memberMapper).should().incrementGame("u1");
        then(memberMapper).should().incrementDraw("u1");
        then(memberMapper).should(never()).incrementWin("u1");
        then(memberMapper).should(never()).incrementLose("u1");
    }
}
