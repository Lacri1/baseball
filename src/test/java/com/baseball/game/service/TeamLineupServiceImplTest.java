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
    @DisplayName("getDefaultLineup: DB에서 라인업 조회 성공")
    void getDefaultLineup_success() {
        List<TeamLineup> dummy = List.of(new TeamLineup());
        given(teamLineupMapper.findDefaultLineupByTeam("Giants")).willReturn(dummy);

        List<TeamLineup> result = service.getDefaultLineup("Giants");

        assertThat(result).isEqualTo(dummy);
    }

    @Test
    @DisplayName("getDefaultLineup: DB에 라인업 없으면 예외 발생")
    void getDefaultLineup_fail() {
        given(teamLineupMapper.findDefaultLineupByTeam("Unknown")).willReturn(Collections.emptyList());

        assertThatThrownBy(() -> service.getDefaultLineup("Unknown"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("saveCustomLineup: 정상 저장 및 getCustomLineup 조회")
    void saveCustomLineup_and_getCustomLineup() {
        String userId = "user1";
        String teamName = "Giants";
        List<CustomLineupRequest.LineupPosition> lineup = new ArrayList<>();
        // 9명의 타자
        String[] batters = {"이대호", "손아섭", "전준우", "안치홍", "강민호", "김주찬", "민병헌", "정훈", "정보근"};
        for (int i = 0; i < 9; i++) {
            CustomLineupRequest.LineupPosition pos = new CustomLineupRequest.LineupPosition();
            pos.setPlayerName(batters[i]);
            pos.setPosition(i + 1);
            lineup.add(pos);
        }
        // 선발 투수
        CustomLineupRequest.LineupPosition pitcherPos = new CustomLineupRequest.LineupPosition();
        pitcherPos.setPlayerName("장원준");
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
            pos.setPlayerName("이대호");
            pos.setPosition(i + 1);
            lineup.add(pos);
        }
        // 투수
        CustomLineupRequest.LineupPosition pitcherPos = new CustomLineupRequest.LineupPosition();
        pitcherPos.setPlayerName("장원준");
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
        List<String> giants = service.getAvailablePlayers("Giants");
        assertThat(giants).contains("이대호", "손아섭");
        List<String> dinos = service.getAvailablePlayers("Dinos");
        assertThat(dinos).contains("박민우", "알테어");
    }

    @Test
    @DisplayName("getBatterByName/getPitcherByName: 선수 객체 반환")
    void getBatterAndPitcherByName() {
        Batter batter = service.getBatterByName("이대호");
        assertThat(batter).isNotNull();
        assertThat(batter.getTeam()).isEqualTo("Giants");

        Pitcher pitcher = service.getPitcherByName("장원준");
        assertThat(pitcher).isNotNull();
        assertThat(pitcher.getTeam()).isEqualTo("Giants");
    }
}