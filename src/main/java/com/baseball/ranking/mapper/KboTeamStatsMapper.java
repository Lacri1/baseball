package com.baseball.ranking.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.baseball.ranking.dto.KboTeamStatsDto;

@Mapper // MyBatis 매퍼 인터페이스임을 나타냅니다.
public interface KboTeamStatsMapper {
    // 승률 기준으로 내림차순 정렬하여 모든 팀 기록 조회 - Dafualt
    List<KboTeamStatsDto> findTeamStatsOrderByWinPercentageDesc();
    
    // 승률 기준으로 내림차순 정렬하여 5팀 기록 조회 - 초기 화면에 출력할 것.
    List<KboTeamStatsDto> findTeamStatsOrderByWinPercentageDescLimitFive();
}