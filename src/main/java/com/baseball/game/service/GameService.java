package com.baseball.game.service;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Pitcher;

public interface GameService {
	// 게임 생성 및 관리
	GameDto createGame(String homeTeam, String awayTeam);
	GameDto getGame(String gameId);
	
	// 타격 관련
	String batterSwing(String gameId, boolean swing);
	String batterSwing(String gameId, boolean swing, double timing);
	String processAtBat(String gameId);
	
	// 투구 관련
	String pitcherThrow(String gameId, String pitchType);
	
	// 게임 상태 관리
	GameDto nextInning(String gameId);
	GameDto endGame(String gameId);
	
	// 베이스 러닝
	void advanceRunners(String gameId, int bases);
	
	
	// 게임 통계
	String getGameStats(String gameId);
}
