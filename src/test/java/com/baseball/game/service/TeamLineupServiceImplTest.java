package com.baseball.game.service;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.CustomLineupRequest;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.TeamLineup;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.mapper.TeamLineupMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class TeamLineupServiceImplTest {

    @InjectMocks
    private TeamLineupServiceImpl service;

    @Mock
    private TeamLineupMapper teamLineupMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getDefaultLineup: 인메모리 기본 라인업 제공(9타자+선발투수)")
    void getDefaultLineup_inMemory() {
        List<TeamLineup> result = service.getDefaultLineup("SSG 랜더스");
        assertThat(result).hasSize(10);
        assertThat(result.stream().filter(t -> t.getPosition().endsWith("_Batter")).count()).isEqualTo(9);
        assertThat(result.stream().anyMatch(t -> "Starting_Pitcher".equals(t.getPosition()))).isTrue();
    }

    @Test
    @DisplayName("saveCustomLineup: 정상 저장 및 getCustomLineup 조회")
    void saveCustomLineup_and_getCustomLineup() {
        String userId = "user1";
        String teamName = "Giants";
        List<CustomLineupRequest.LineupPosition> lineup = new ArrayList<>();
        // 9명의 타자
        String[] batters = {"황성빈", "윤동희", "레이예스", "전준우", "나승엽", "손호영", "손성빈", "고승민", "박승욱"};
        for (int i = 0; i < 9; i++) {
            CustomLineupRequest.LineupPosition pos = new CustomLineupRequest.LineupPosition();
            pos.setPlayerName(batters[i]);
            pos.setPosition(i + 1);
            lineup.add(pos);
        }
        // 선발 투수
        CustomLineupRequest.LineupPosition pitcherPos = new CustomLineupRequest.LineupPosition();
        pitcherPos.setPlayerName("박세웅");
        pitcherPos.setPosition(null); // 투수는 position 필요 없음
        lineup.add(pitcherPos);

        CustomLineupRequest req = new CustomLineupRequest();
        req.setUserId(userId);
        req.setTeamName(teamName);
        req.setLineup(lineup);

        service.saveCustomLineup(req);

        List<TeamLineup> result = service.getCustomLineup(userId, teamName);
        assertThat(result).hasSize(10); // 9타자+1투수
        assertThat(result.stream().anyMatch(tl -> "Starting_Pitcher".equals(tl.getPosition()))).isTrue();
    }

    @Test
    @DisplayName("saveCustomLineup: 타자 9명 미만이면 예외")
    void saveCustomLineup_lessThan9Batters() {
        String userId = "user2";
        String teamName = "Giants";
        List<CustomLineupRequest.LineupPosition> lineup = new ArrayList<>();
        // 8명만 추가
        for (int i = 0; i < 8; i++) {
            CustomLineupRequest.LineupPosition pos = new CustomLineupRequest.LineupPosition();
            pos.setPlayerName("황성빈");
            pos.setPosition(i + 1);
            lineup.add(pos);
        }
        // 투수
        CustomLineupRequest.LineupPosition pitcherPos = new CustomLineupRequest.LineupPosition();
        pitcherPos.setPlayerName("박세웅");
        lineup.add(pitcherPos);

        CustomLineupRequest req = new CustomLineupRequest();
        req.setUserId(userId);
        req.setTeamName(teamName);
        req.setLineup(lineup);

        assertThatThrownBy(() -> service.saveCustomLineup(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("9명의 타자");
    }

    @Test
    @DisplayName("getAvailablePlayers: 팀별 타자 이름 반환")
    void getAvailablePlayers() {
        List<String> giants = service.getAvailablePlayers("롯데 자이언츠");
        assertThat(giants).contains("황성빈", "윤동희");
        List<String> dinos = service.getAvailablePlayers("NC 다이노스");
        assertThat(dinos).contains("박민우", "서호철");
    }

    @Test
    @DisplayName("getBatterByName/getPitcherByName: 선수 객체 반환")
    void getBatterAndPitcherByName() {
        Batter batter = service.getBatterByName("황성빈");
        assertThat(batter).isNotNull();
        assertThat(batter.getTeam()).isEqualTo("Giants");

        Pitcher pitcher = service.getPitcherByName("박세웅");
        assertThat(pitcher).isNotNull();
        assertThat(pitcher.getTeam()).isEqualTo("Giants");
    }
}