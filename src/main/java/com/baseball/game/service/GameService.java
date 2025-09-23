package com.baseball.game.service;
import com.baseball.game.dto.GameDto;

public interface GameService {
	// 게임 생성 및 관리
	GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense);
	GameDto getGame(String gameId);
	void resetGame(String gameId);

	// 타격 관련
	String batterSwing(String gameId, boolean swing);
	String batterSwing(String gameId, boolean swing, double timing);

	// 투구 관련
	String pitcherThrow(String gameId, String pitchType);
	String playComputerTurn(String gameId);

	// 게임 상태 관리
	GameDto nextInning(String gameId);
	GameDto endGame(String gameId);

	// 베이스 러닝
	void advanceRunners(String gameId, int bases);

	// 게임 통계
	String getGameStats(String gameId);

	// 라인업 설정
	void setLineup(String gameId, com.baseball.game.dto.LineupRequest request);
}

