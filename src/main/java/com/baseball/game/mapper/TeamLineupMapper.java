package com.baseball.game.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baseball.game.dto.TeamLineup;

@Mapper
public interface TeamLineupMapper {

    // 기본 라인업 조회 (컴퓨터용)
    List<TeamLineup> findDefaultLineupByTeam(@Param("teamName") String teamName);
    // 팀별 사용 가능한 선수 목록 조회
    List<String> findAvailablePlayersByTeam(@Param("teamName") String teamName);
}