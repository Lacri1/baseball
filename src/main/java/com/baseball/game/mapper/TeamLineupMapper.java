package com.baseball.game.mapper;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.TeamStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeamLineupMapper {
    List<String> findAvailablePlayersByTeamBatters(@Param("teamName") String teamName);

    List<String> findAvailablePlayersByTeamPitchers(@Param("teamName") String teamName);

    List<Batter> findAvailableBattersByTeam(@Param("teamName") String teamName);

    List<Pitcher> findAvailablePitchersByTeam(@Param("teamName") String teamName);

    List<Batter> findAllBatters(@Param("sortBy") String sortBy);

    List<Pitcher> findAllPitchers(@Param("sortBy") String sortBy);

    List<TeamStats> findAllTeamStats();
}
