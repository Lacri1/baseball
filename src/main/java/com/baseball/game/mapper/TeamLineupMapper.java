package com.baseball.game.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;

@Mapper
public interface TeamLineupMapper {
    // 팀별 사용 가능한 선수 목록 조회 (이름만)
    List<String> findAvailablePlayersByTeamBatters(@Param("teamName") String teamName);

    List<String> findAvailablePlayersByTeamPitchers(@Param("teamName") String teamName);

    // 팀별 사용 가능한 선수 성적 데이터 조회
    List<Batter> findAvailableBattersByTeam(@Param("teamName") String teamName);

    List<Pitcher> findAvailablePitchersByTeam(@Param("teamName") String teamName);
}