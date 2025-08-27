package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.MemberDto;
import com.baseball.game.mapper.MemberMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Sql(scripts = { "classpath:schema.sql", "classpath:data.sql" }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class GameResultIntegrationTest {

    @Autowired
    private GameLifecycleService lifecycle;
    @Autowired
    private GameStateService state;
    @Autowired
    private MemberMapper memberMapper;

    @Test
    void endGame_updatesMemberCounts_win() {
        GameDto game = lifecycle.createGame("H", "A", 9, false); // 사용자=홈
        game.setUserId("u1");
        game.setHomeScore(3);
        game.setAwayScore(1);

        state.endGame(game.getGameId());

        MemberDto m = memberMapper.member("u1");
        assertThat(m.getGame()).isEqualTo(1);
        assertThat(m.getWin()).isEqualTo(1);
        assertThat(m.getLose()).isEqualTo(0);
        assertThat(m.getDraw()).isEqualTo(0);
    }

    @Test
    void endGame_updatesMemberCounts_lose() {
        GameDto game = lifecycle.createGame("H", "A", 9, false); // 사용자=홈
        game.setUserId("u1");
        game.setHomeScore(1);
        game.setAwayScore(3);

        state.endGame(game.getGameId());

        MemberDto m = memberMapper.member("u1");
        assertThat(m.getGame()).isEqualTo(1);
        assertThat(m.getWin()).isEqualTo(0);
        assertThat(m.getLose()).isEqualTo(1);
        assertThat(m.getDraw()).isEqualTo(0);
    }

    @Test
    void endGame_updatesMemberCounts_draw() {
        GameDto game = lifecycle.createGame("H", "A", 9, false); // 사용자=홈
        game.setUserId("u1");
        game.setHomeScore(2);
        game.setAwayScore(2);

        state.endGame(game.getGameId());

        MemberDto m = memberMapper.member("u1");
        assertThat(m.getGame()).isEqualTo(1);
        assertThat(m.getWin()).isEqualTo(0);
        assertThat(m.getLose()).isEqualTo(0);
        assertThat(m.getDraw()).isEqualTo(1);
    }
}
