package com.baseball.game.ranking.service;

import java.util.List;

import com.baseball.game.ranking.dto.KboHitterStatsDto;
import com.baseball.game.ranking.dto.KboPitcherStatsDto;
import com.baseball.game.ranking.dto.KboTeamStatsDto;

public interface KboStatsService {
	// --- 타자 기록 관련 메서드 ---
    public List<KboHitterStatsDto> getHitterStatsOrderByBattingAverage(); // 타율 기준으로 내림차순 정렬하여 모든 타자 기록 조회 - Default
    public List<KboHitterStatsDto> getHitterStatsOrderByNumberOfGamesDesc(); // 경기 수 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByPlateAppearanceDesc(); // 타석 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByRunDesc(); // 득점 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByHitDesc(); // 안타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByTwoBaseDesc(); // 2루타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByThreeBaseDesc(); // 3루타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByHomeRun(); // 홈런 기준으로 내림차순 정렬하여 모든 타자 기록 조회 
    public List<KboHitterStatsDto> getHitterStatsOrderByRunsBattedIn(); // 타점 기준으로 내림차순 정렬하여 모든 타자 기록 조회 
    public List<KboHitterStatsDto> getHitterStatsOrderByFourBallDesc(); // 볼넷 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByStrikeOutDesc(); // 삼진 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByOnBasePercentageDesc(); // 출루율 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboHitterStatsDto> getHitterStatsOrderByOPSDesc(); // OPS 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    
    public List<KboHitterStatsDto> getHitterStatsOrderByBattingAverageLimitFive(); // 타율 기준으로 내림차순 정렬하여 모든 타자 기록 조회 - Default

    // --- 투수 기록 관련 메서드 ---
    public List<KboPitcherStatsDto> getPitcherStatsOrderByERADesc(); // 평균자책점 기준으로 오름차순 정렬하여 모든 투수 기록 조회 - Default
    public List<KboPitcherStatsDto> getPitcherStatsOrderByNumberOfGameDesc(); // 경기 수 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByWin(); // 승을 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByLoseDesc(); // 패를 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderBySaveDesc(); // 세이브 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByHoldDesc(); // 홀드 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByNumberOfInningDesc(); // 이닝 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByHitDesc(); // 피안타 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByHomeRunDesc(); // 피홈런 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByFourBallDesc(); // 볼넷 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByStrikeOut(); // 탈삼진 기준으로 내림차순 정렬하여 모든 투수 기록 조회\
    public List<KboPitcherStatsDto> getPitcherStatsOrderByRunDesc(); // 실점 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByEarnedRunDesc(); // 자책 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    public List<KboPitcherStatsDto> getPitcherStatsOrderByWHIPAsc(); // WHIP 기준으로 오름차순 정렬하여 모든 투수 기록 조회
    
    public List<KboPitcherStatsDto> getPitcherStatsOrderByERADescLimitFive(); // 평균자책점 기준으로 오름차순 정렬하여 모든 투수 기록 조회 - Default

    // --- 팀 기록 관련 메서드 ---
    public List<KboTeamStatsDto> getTeamStatsOrderByWinPercentage(); // Default
    
    public List<KboTeamStatsDto> getTeamStatsOrderByWinPercentageLimitFive(); // Default
}
