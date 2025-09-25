package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.TeamLineupSetRequest;

/**
 * 게임 서비스 인터페이스
 */
public interface GameService {
    
    /**
     * 새로운 게임을 생성합니다.
     */
    GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense);
    
    /**
     * 게임 ID로 게임을 조회합니다.
     */
    GameDto getGame(String gameId);
    
    /**
     * 타자가 스윙합니다.
     */
    String batterSwing(String gameId, Boolean swing, Boolean timing);
    
    /**
     * 투수가 투구합니다.
     */
    String pitcherThrow(String gameId, String pitchType);
    
    /**
     * 컴퓨터의 턴을 진행합니다.
     */
    String playComputerTurn(String gameId);
    
    /**
     * 다음 이닝으로 진행합니다.
     */
    GameDto nextInning(String gameId);
    
    /**
     * 게임을 종료합니다.
     */
    GameDto endGame(String gameId);
    
    /**
     * 베이스 러너를 진루시킵니다.
     */
    void advanceRunners(String gameId, Integer basesToAdvance);
    
    /**
     * 게임을 리셋합니다.
     */
    void resetGame(String gameId);

    /**
     * 특정 게임의 홈/원정 팀 라인업과 선발 투수를 설정합니다.
     * request.teamName 이 게임의 homeTeam/awayTeam 중 하나여야 하며,
     * 타자 9명과 선발 투수 1명을 포함해야 합니다.
     */
    GameDto applyTeamLineup(String gameId, TeamLineupSetRequest request);
}