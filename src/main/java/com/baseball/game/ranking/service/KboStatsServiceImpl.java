package com.baseball.game.ranking.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baseball.game.ranking.dto.KboHitterStatsDto;
import com.baseball.game.ranking.dto.KboPitcherStatsDto;
import com.baseball.game.ranking.dto.KboTeamStatsDto;
import com.baseball.game.ranking.mapper.KboHitterStatsMapper;
import com.baseball.game.ranking.mapper.KboPitcherStatsMapper;
import com.baseball.game.ranking.mapper.KboTeamStatsMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service // Spring 서비스 컴포넌트임을 나타냅니다.
public class KboStatsServiceImpl implements KboStatsService{
	private final KboHitterStatsMapper hitterStatsMapper;
    private final KboPitcherStatsMapper pitcherStatsMapper;
    private final KboTeamStatsMapper teamStatsMapper;

    @Autowired
    public KboStatsServiceImpl(KboHitterStatsMapper hitterStatsMapper,
            KboPitcherStatsMapper pitcherStatsMapper, KboTeamStatsMapper teamStatsMapper) 
    {
        this.hitterStatsMapper = hitterStatsMapper;
        this.pitcherStatsMapper = pitcherStatsMapper;
        this.teamStatsMapper = teamStatsMapper;
    }

    // Helper method to get team game counts
    private java.util.Map<String, Integer> getTeamGameCounts() {
        List<KboTeamStatsDto> teamStats = teamStatsMapper.findTeamStatsOrderByWinPercentageDesc();
        java.util.Map<String, Integer> teamGameCounts = new java.util.HashMap<>();
        for (KboTeamStatsDto team : teamStats) {
            teamGameCounts.put(team.getTeamName(), team.getGameNum());
        }
        return teamGameCounts;
    }

    // --- 타자 기록 관련 method ---
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByBattingAverage() { // 타율 기준으로 내림차순 정렬하여 모든 타자 기록 조회 - Default
        java.util.Map<String, Integer> teamGameCounts = getTeamGameCounts();
        List<KboHitterStatsDto> allHitterStats = hitterStatsMapper.findHitterStatsOrderByBattingAverageDesc();
        List<KboHitterStatsDto> qualifiedHitterStats = new java.util.ArrayList<>();

        for (KboHitterStatsDto hitter : allHitterStats) {
            Integer gameNum = teamGameCounts.get(hitter.getPlayerTeam());
            if (gameNum != null) {
                double requiredPlateAppearance = gameNum * 3.1;
                log.info("Hitter: {}, Team: {}, GameNum: {}, Required PA: {}, Actual PA: {}", 
                         hitter.getPlayerName(), hitter.getPlayerTeam(), gameNum, requiredPlateAppearance, hitter.getPlateAppearance());
                if (hitter.getPlateAppearance() != null && hitter.getPlateAppearance() >= requiredPlateAppearance) {
                    qualifiedHitterStats.add(hitter);
                }
            } else {
                log.warn("Team game count not found for hitter: {} (Team: {})", hitter.getPlayerName(), hitter.getPlayerTeam());
            }
        }
        return qualifiedHitterStats;
    } 
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByNumberOfGamesDesc(){ // 경기 수 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByNumberOfGamesDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByPlateAppearanceDesc(){ // 타석 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByPlateAppearanceDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByRunDesc(){ // 득점 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByRunDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByHitDesc(){ // 안타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByHitDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByTwoBaseDesc(){ // 2루타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByTwoBaseDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByThreeBaseDesc(){ // 3루타 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByThreeBaseDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByHomeRun() { // 홈런 기준으로 내림차순 정렬하여 모든 타자 기록 조회
        return hitterStatsMapper.findHitterStatsOrderByHomeRunDesc();
    }  
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByRunsBattedIn() { // 타점 기준으로 내림차순 정렬하여 모든 타자 기록 조회
        return hitterStatsMapper.findHitterStatsOrderByRunsBattedInDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByFourBallDesc(){ // 볼넷 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByFourBallDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByStrikeOutDesc(){ // 삼진 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByStrikeOutDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByOnBasePercentageDesc(){ // 출루율 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByOnBasePercentageDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByOPSDesc(){ // OPS 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return hitterStatsMapper.findHitterStatsOrderByOPSDesc();
    }
    
    @Override
    public List<KboHitterStatsDto> getHitterStatsOrderByBattingAverageLimitFive() { // 타율 기준으로 내림차순 명 정렬하여 모든 타자 기록 조회 - 초기 화면
        java.util.Map<String, Integer> teamGameCounts = getTeamGameCounts();
        List<KboHitterStatsDto> allHitterStats = hitterStatsMapper.findHitterStatsOrderByBattingAverageDescLimitFive();
        List<KboHitterStatsDto> qualifiedHitterStats = new java.util.ArrayList<>();

        for (KboHitterStatsDto hitter : allHitterStats) {
            Integer gameNum = teamGameCounts.get(hitter.getPlayerTeam());
            if (gameNum != null) {
                double requiredPlateAppearance = gameNum * 3.1;
                log.info("Hitter (LimitFive): {}, Team: {}, GameNum: {}, Required PA: {}, Actual PA: {}", 
                         hitter.getPlayerName(), hitter.getPlayerTeam(), gameNum, requiredPlateAppearance, hitter.getPlateAppearance());
                if (hitter.getPlateAppearance() != null && hitter.getPlateAppearance() >= requiredPlateAppearance) {
                    qualifiedHitterStats.add(hitter);
                }
            } else {
                log.warn("Team game count not found for hitter (LimitFive): {} (Team: {})", hitter.getPlayerName(), hitter.getPlayerTeam());
            }
        }
        return qualifiedHitterStats.stream().limit(5).collect(Collectors.toList());
    } 
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    // --- 투수 기록 관련 method ---    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByERADesc(){ // 평균자책점 기준으로 오름차순 정렬하여 모든 투수 기록 조회 - Default
        java.util.Map<String, Integer> teamGameCounts = getTeamGameCounts();
        List<KboPitcherStatsDto> allPitcherStats = pitcherStatsMapper.findPitcherStatsOrderByEarnedRunAverageAsc();
        List<KboPitcherStatsDto> qualifiedPitcherStats = new java.util.ArrayList<>();

        for (KboPitcherStatsDto pitcher : allPitcherStats) {
            Integer gameNum = teamGameCounts.get(pitcher.getPlayerTeam());
            if (gameNum != null) {
                double requiredInningsPitched = gameNum * 1.0;
                log.info("Pitcher: {}, Team: {}, GameNum: {}, Required IP: {}, Actual IP: {}", 
                         pitcher.getPlayerName(), pitcher.getPlayerTeam(), gameNum, requiredInningsPitched, pitcher.getInningsPitched());
                if (pitcher.getInningsPitched() != null && pitcher.getInningsPitched() >= requiredInningsPitched) {
                    qualifiedPitcherStats.add(pitcher);
                }
            } else {
                log.warn("Team game count not found for pitcher: {} (Team: {})", pitcher.getPlayerName(), pitcher.getPlayerTeam());
            }
        }
        return qualifiedPitcherStats;
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByNumberOfGameDesc(){ // 경기 수 기준으로 내림차순 정렬하여 모든 타자 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByNumberOfGameDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByWin(){ // 승을 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByWinDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByLoseDesc(){ // 패를 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByLoseDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderBySaveDesc(){ // 세이브 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderBySaveDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByHoldDesc(){ // 홀드 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByHoldDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByNumberOfInningDesc(){ // 이닝 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByNumberOfInningDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByHitDesc(){ // 피안타 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByHitDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByHomeRunDesc(){ // 피홈런 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByHomeRunDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByFourBallDesc(){ // 볼넷 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByFourBallDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByStrikeOut(){ // 탈삼진 기준으로 내림차순 정렬하여 모든 투수 기록 조회\
    	return pitcherStatsMapper.findPitcherStatsOrderByStrikeOutDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByRunDesc(){ // 실점 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByRunDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByEarnedRunDesc(){ // 자책 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByEarnedRunDesc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByWHIPAsc(){ // WHIP 기준으로 내림차순 정렬하여 모든 투수 기록 조회
    	return pitcherStatsMapper.findPitcherStatsOrderByWHIPAsc();
    }
    
    @Override
    public List<KboPitcherStatsDto> getPitcherStatsOrderByERADescLimitFive(){ // 평균자책점 기준으로 오름차순 정렬하여 모든 투수 기록 조회 - Default
        java.util.Map<String, Integer> teamGameCounts = getTeamGameCounts();
        List<KboPitcherStatsDto> allPitcherStats = pitcherStatsMapper.findPitcherStatsOrderByEarnedRunAverageAscLimitFive();
        List<KboPitcherStatsDto> qualifiedPitcherStats = new java.util.ArrayList<>();

        for (KboPitcherStatsDto pitcher : allPitcherStats) {
            Integer gameNum = teamGameCounts.get(pitcher.getPlayerTeam());
            if (gameNum != null) {
                double requiredInningsPitched = gameNum * 1.0;
                log.info("Pitcher (LimitFive): {}, Team: {}, GameNum: {}, Required IP: {}, Actual IP: {}", 
                         pitcher.getPlayerName(), pitcher.getPlayerTeam(), gameNum, requiredInningsPitched, pitcher.getInningsPitched());
                if (pitcher.getInningsPitched() != null && pitcher.getInningsPitched() >= requiredInningsPitched) {
                    qualifiedPitcherStats.add(pitcher);
                }
            } else {
                log.warn("Team game count not found for pitcher (LimitFive): {} (Team: {})", pitcher.getPlayerName(), pitcher.getPlayerTeam());
            }
        }
        return qualifiedPitcherStats.stream().limit(5).collect(java.util.stream.Collectors.toList());
    }
    
	//////////////////////////////////////////////////////////////////////////////////////////////////

    // --- 팀 기록 관련 method ---
    @Override
    public List<KboTeamStatsDto> getTeamStatsOrderByWinPercentage() { // 승률 기준으로 내림차순 정렬하여 모든 팀 기록 조회 - Dafualt
        return teamStatsMapper.findTeamStatsOrderByWinPercentageDesc();
    }
    
    @Override
    public List<KboTeamStatsDto> getTeamStatsOrderByWinPercentageLimitFive() { // 승률 기준으로 내림차순 정렬하여 모든 팀 기록 조회 - Dafualt
        return teamStatsMapper.findTeamStatsOrderByWinPercentageDescLimitFive();
    }
}