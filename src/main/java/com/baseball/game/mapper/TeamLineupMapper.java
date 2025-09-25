package com.baseball.game.mapper;

import com.baseball.game.dto.TeamLineup;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.TeamStats;

import java.util.List;

@Mapper
public interface TeamLineupMapper {
    // 팀별 사용 가능한 선수 목록 조회 (이름만)
    List<String> findAvailablePlayersByTeamBatters(@Param("teamName") String teamName);

    List<String> findAvailablePlayersByTeam(@Param("teamName") String teamName);

    List<String> findAvailablePlayersByTeamPitchers(@Param("teamName") String teamName);

    // 팀별 사용 가능한 선수 성적 데이터 조회
    List<Batter> findAvailableBattersByTeam(@Param("teamName") String teamName);

    List<Pitcher> findAvailablePitchersByTeam(@Param("teamName") String teamName);

    List<Batter> getAllBattersWithStats(@Param("sortBy") String sortBy);

    List<Pitcher> getAllPitchersWithStats(@Param("sortBy") String sortBy);

    List<TeamStats> getAllTeamStats();

    void deleteCustomLineupByUserAndTeam(@Param("userId") String userId, @Param("teamName") String teamName);

    void insertCustomLineup(TeamLineup teamLineup);

    List<TeamLineup> findDefaultLineupByTeam(@Param("teamName") String teamName);

    List<TeamLineup> findCustomLineupByUserAndTeam(@Param("userId") String userId, @Param("teamName") String teamName);
}