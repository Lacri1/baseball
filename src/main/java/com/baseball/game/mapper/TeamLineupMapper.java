package com.baseball.game.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TeamLineupMapper {
    // 팀별 사용 가능한 선수 목록 조회
    List<String> findAvailablePlayersByTeamBatters(@Param("teamName") String teamName);

    List<String> findAvailablePlayersByTeamPitchers(@Param("teamName") String teamName);
}