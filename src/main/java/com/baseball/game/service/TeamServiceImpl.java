package com.baseball.game.service;

import com.baseball.game.dto.Player;
// TeamMapper 제거: DB 매퍼 대신 TeamLineupService를 사용해 선수 목록 제공
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
    // TeamMapper 제거됨

    @Setter(onMethod_ = @Autowired)
    private TeamLineupService lineupService;

    @Override
    public List<Player> getPlayersByTeam(String teamId) {
        // 인메모리 라인업에서 타자 이름 목록을 가져와 Player 리스트로 변환
        List<String> names = lineupService != null ? lineupService.getAvailablePlayers(teamId) : java.util.Collections.emptyList();
        List<Player> fallback = new ArrayList<>();
        for (String name : names) {
            Batter b = new Batter(name, teamId);
            fallback.add(b);
        }
        return fallback;
    }

    @Override
    public TeamRoster getRosterByTeam(String teamId) {
        // 타자: 기존 로직 재사용
        List<Player> battersAsPlayers = getPlayersByTeam(teamId);
        List<Pitcher> pitchers;
        // 투수: TeamLineupService에서 가용 투수명 조회 후 Pitcher 객체로 변환
        List<String> pitcherNames = lineupService != null ? lineupService.getAvailablePitchers(teamId) : java.util.Collections.emptyList();
        java.util.List<Pitcher> list = new java.util.ArrayList<>();
        for (String name : pitcherNames) {
            Pitcher p = new Pitcher(name, teamId);
            list.add(p);
        }
        pitchers = list;

        // 변환: Player 리스트 중 Batter만 유지하여 TeamRoster.batters에 담음
        java.util.List<Player> batters = battersAsPlayers;

        return TeamRoster.builder()
                .teamId(teamId)
                .batters(batters)
                .pitchers(pitchers)
                .build();
    }
    // registerLineup 메서드 삭제
}