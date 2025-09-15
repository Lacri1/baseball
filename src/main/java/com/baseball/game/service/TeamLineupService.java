package com.baseball.game.service;

import java.util.List;
import com.baseball.game.dto.TeamLineup;
import com.baseball.game.dto.CustomLineupRequest;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.TeamStats;

public interface TeamLineupService {

    // 기본 라인업 조회 (컴퓨터용) - 인메모리 제공
    List<TeamLineup> getDefaultLineup(String teamName);

    // 유저 커스텀 라인업 조회
    List<TeamLineup> getCustomLineup(String userId, String teamName);

    // 커스텀 라인업 저장
    void saveCustomLineup(CustomLineupRequest request);

    // 유저의 모든 커스텀 라인업 조회
    List<TeamLineup> getAllCustomLineups(String userId);

    // 팀별 사용 가능한 선수 목록 조회
    List<String> getAvailablePlayers(String teamName);

    // 팀별 사용 가능한 투수 목록 조회 (이름만)
    List<String> getAvailablePitchers(String teamName);

    // 성적 포함: 팀별 사용 가능한 타자 목록 조회
    List<Batter> getAvailableBattersWithStats(String teamName);

    // 성적 포함: 팀별 사용 가능한 투수 목록 조회
    List<Pitcher> getAvailablePitchersWithStats(String teamName);

    // 모든 타자 성적 데이터 조회
    List<Batter> getAllBattersWithStats(String sortBy);

    // 모든 투수 성적 데이터 조회
    List<Pitcher> getAllPitchersWithStats(String sortBy);

    // 모든 팀 성적 데이터 조회
    List<TeamStats> getAllTeamStats();
}