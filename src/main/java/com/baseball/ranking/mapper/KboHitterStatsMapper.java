package com.baseball.ranking.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.baseball.ranking.dto.KboHitterStatsDto;

@Mapper // MyBatis 매퍼 인터페이스임을 나타냅니다.
public interface KboHitterStatsMapper {
    // 타율 기준으로 내림차순 정렬하여 모든 타자 기록 조회 - Default
    List<KboHitterStatsDto> findHitterStatsOrderByBattingAverageDesc();
    
	// 경기 수 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByNumberOfGamesDesc();
    
    // 타석 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByPlateAppearanceDesc();

    // 득점 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByRunDesc();
	
    // 안타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByHitDesc();
	
    // 2루타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByTwoBaseDesc();
	
    // 3루타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByThreeBaseDesc();
    
    // 홈런 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByHomeRunDesc();

    // 타점 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByRunsBattedInDesc();
    
	// 볼넷 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByFourBallDesc();
    
	// 삼진 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByStrikeOutDesc();
    
	// 출루율 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByOnBasePercentageDesc();
	
    // OPS 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    List<KboHitterStatsDto> findHitterStatsOrderByOPSDesc();
    
    // 	타율 기준으로 내림차순 정렬하여 5명의 타자 기록 조회 - 초기 화면에 출력할 것.
    List<KboHitterStatsDto> findHitterStatsOrderByBattingAverageDescLimitFive();
}


