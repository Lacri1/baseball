package com.baseball.game.ranking.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

import com.baseball.game.ranking.dto.KboPitcherStatsDto;

@Mapper // MyBatis 매퍼 인터페이스임을 나타냅니다.
public interface KboPitcherStatsMapper {
    // 평균자책점 기준으로 오름차순 정렬하여 모든 투수 기록 조회 - Default
    List<KboPitcherStatsDto> findPitcherStatsOrderByEarnedRunAverageAsc();
    
    // 경기 수 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByNumberOfGameDesc();
    
    // 승을 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByWinDesc();
    
    // 패를 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByLoseDesc();
    
    // 세이브 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderBySaveDesc();
    
    // 홀드 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByHoldDesc();
    
    // 이닝 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByNumberOfInningDesc();
    
    // 피안타 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByHitDesc();
    
    // 피홈런 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByHomeRunDesc();
    
    // 볼넷 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByFourBallDesc();
    
    // 탈삼진 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByStrikeOutDesc();
    
    // 실점 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByRunDesc();
    
    // 자책 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByEarnedRunDesc();
    
    // WHIP 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    List<KboPitcherStatsDto> findPitcherStatsOrderByWHIPAsc();
    
    // 평균자책점 기준으로 오름차순 정렬하여 5명의 투수 기록 조회 - 초기 화면에 출력할 것.
    List<KboPitcherStatsDto> findPitcherStatsOrderByEarnedRunAverageAscLimitFive();
    
}
