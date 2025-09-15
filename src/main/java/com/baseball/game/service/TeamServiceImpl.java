package com.baseball.game.service;

import com.baseball.game.dto.Player;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.TeamRoster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;
import lombok.Setter;

@Service
public class TeamServiceImpl implements TeamService {
    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    @Setter(onMethod_ = @Autowired)
    private TeamLineupService lineupService;

    

    @Override
    public List<String> getTeamList() {
        List<String> teams = new ArrayList<>();
        teams.add("KIA 타이거즈");
        teams.add("삼성 라이온즈");
        teams.add("LG 트윈스");
        teams.add("두산 베어스");
        teams.add("kt 위즈");
        teams.add("SSG 랜더스");
        teams.add("롯데 자이언츠");
        teams.add("한화 이글스");
        teams.add("NC 다이노스");
        teams.add("키움 히어로즈");
        return teams;
    }

    @Override
    public List<Player> getPlayersByTeam(String teamId) {
        List<Player> players = new ArrayList<>();

        try {
            // TeamLineupService의 성적 포함 메서드 사용
            List<Batter> batters = lineupService != null ? lineupService.getAvailableBattersWithStats(teamId)
                    : java.util.Collections.emptyList();
            if (batters != null && !batters.isEmpty()) {
                players.addAll(batters);
                log.info("팀 {}의 타자 {}명을 LineupService에서 조회했습니다.", teamId, batters.size());
                return players;
            }
        } catch (Exception e) {
            log.warn("팀 {}의 타자 LineupService 조회 실패: {}", teamId, e.getMessage());
        }

        // 폴백: 인메모리 라인업 사용 (이름만)
        List<String> names = lineupService != null ? lineupService.getAvailablePlayers(teamId)
                : java.util.Collections.emptyList();
        for (String name : names) {
            Batter b = new Batter(name, teamId);
            players.add(b);
        }
        log.info("팀 {}의 타자 {}명을 인메모리에서 조회했습니다.", teamId, names.size());
        return players;
    }

        @Override
    public TeamRoster getRosterByTeam(String teamId) {
        log.info("======================================================");
        log.info("[DEBUG] getRosterByTeam 호출됨. teamId: {}", teamId);

        // 타자: LineupService 성적 포함 메서드 사용
        List<Batter> batters = new ArrayList<>();
        try {
            batters = lineupService != null ? lineupService.getAvailableBattersWithStats(teamId)
                    : java.util.Collections.emptyList();
            if (batters != null && !batters.isEmpty()) {
                log.info("팀 {}의 타자 {}명을 LineupService에서 조회했습니다.", teamId, batters.size());
            }
        } catch (Exception e) {
            log.warn("팀 {}의 타자 LineupService 조회 실패: {}", teamId, e.getMessage());
        }

        // 투수: LineupService 성적 포함 메서드 사용
        List<Pitcher> pitchers = new ArrayList<>();
        try {
            pitchers = lineupService != null ? lineupService.getAvailablePitchersWithStats(teamId)
                    : java.util.Collections.emptyList();
            if (pitchers != null && !pitchers.isEmpty()) {
                log.info("팀 {}의 투수 {}명을 LineupService에서 조회했습니다.", teamId, pitchers.size());
            }
        } catch (Exception e) {
            log.warn("팀 {}의 투수 LineupService 조회 실패: {}", teamId, e.getMessage());
        }

        // 폴백: 인메모리 데이터 사용
        if (batters.isEmpty()) {
            List<String> batterNames = lineupService != null ? lineupService.getAvailablePlayers(teamId)
                    : java.util.Collections.emptyList();
            for (String name : batterNames) {
                batters.add(new Batter(name, teamId));
            }
            log.info("팀 {}의 타자 {}명을 인메모리에서 폴백으로 생성했습니다.", teamId, batters.size());
        }

        if (pitchers.isEmpty()) {
            List<String> pitcherNames = lineupService != null ? lineupService.getAvailablePitchers(teamId)
                    : java.util.Collections.emptyList();
            for (String name : pitcherNames) {
                pitchers.add(new Pitcher(name, teamId));
            }
            log.info("팀 {}의 투수 {}명을 인메모리에서 폴백으로 생성했습니다.", teamId, pitchers.size());
        }

                log.info("[DEBUG] 최종 반환: 타자 {}명, 투수 {}명", batters.size(), pitchers.size());
        log.info("======================================================");

        return TeamRoster.builder()

                .teamId(teamId)
                .batters(new ArrayList<>(batters))
                .pitchers(pitchers)
                .build();
    }
}