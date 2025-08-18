package com.baseball.game.mapper;

import com.baseball.game.dto.MemberDto;
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
class MemberMapperIntegrationTest {

    @Autowired
    private MemberMapper memberMapper;

    @Test
    void increments() {
        memberMapper.incrementGame("u1");
        memberMapper.incrementWin("u1");
        memberMapper.incrementLose("u1");
        memberMapper.incrementDraw("u1");

        MemberDto m = memberMapper.member("u1");
        assertThat(m.getGame()).isEqualTo(1);
        assertThat(m.getWin()).isEqualTo(1);
        assertThat(m.getLose()).isEqualTo(1);
        assertThat(m.getDraw()).isEqualTo(1);
    }
}
