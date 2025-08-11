package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
	private String gameId;
	private String homeTeam;
	private String awayTeam;
	private int inning;
	private int maxInning;
	private boolean isTop; // true: 초(원정팀 공격), false: 말(홈팀 공격)
	private int out;
	private int strike;
	private int ball;
	private int homeScore;
	private int awayScore;
	private List<Batter> baseRunners; // 베이스 러너들
	private Batter currentBatter; // 현재 타석에 있는 타자
	private Pitcher currentPitcher; // 현재 마운드에 있는 투수
	private Batter[] bases; // 0: 홈, 1: 1루, 2: 2루, 3: 3루
	private boolean gameOver;
	private String winner;
	private List<Batter> battingOrder;
	private Pitcher startingPitcher;
	// 새로 추가: 홈팀 및 원정팀의 타순 (Batter 객체 리스트)
	private List<Batter> homeBattingOrder;
	private List<Batter> awayBattingOrder;

	// 새로 추가: 홈팀 및 원정팀의 선발 투수 (Pitcher 객체)
	private Pitcher homeStartingPitcher;
	private Pitcher awayStartingPitcher;

	// 기존 필드 유지
	private List<Pitcher> pitcherList; // (팀 전체 투수 목록, 필요 시 사용)
	private boolean isUserOffense; // 사용자가 공격 팀인지 여부 (초기 게임 생성 시 설정, 사용자의 팀이 어느 팀인지 저장)

	private int currentBatterIndex; // 현재 타순 인덱스 (현재 공격 팀의 라인업 기준)

	// 상수 정의
	public static final int MAX_BASES = 4;
	public static final int MAX_OUTS = 3;
	public static final int MAX_STRIKES = 3;
	public static final int MAX_BALLS = 4;

	public GameDto(String gameId, String homeTeam, String awayTeam, int maxinning, int inning, boolean isTop,
			boolean gameOver, String winner) {
		this.gameId = gameId;
		this.homeTeam = homeTeam;
		this.awayTeam = awayTeam;
		this.maxInning = maxinning;
		this.inning = inning;
		this.isTop = isTop;
		this.gameOver = gameOver;
		this.winner = winner;
	}

	// 기본 생성자에서 초기화
	{
		this.baseRunners = new ArrayList<>();
		this.bases = new Batter[MAX_BASES]; // 홈, 1루, 2루, 3루
		this.inning = 1;
		this.isTop = true; // 기본값: 1회 초 (원정팀 공격)
		this.out = 0;
		this.strike = 0;
		this.ball = 0;
		this.homeScore = 0;
		this.awayScore = 0;
		this.gameOver = false;
		this.homeBattingOrder = new ArrayList<>(); // 초기화
		this.awayBattingOrder = new ArrayList<>(); // 초기화
		this.pitcherList = new ArrayList<>(); // 초기화 (필요 시 사용)
		this.currentBatterIndex = 0;
	}

	// 현재 공격 팀의 타순을 반환하는 헬퍼 메서드
	public List<Batter> getCurrentOffensiveLineup() {
		if (this.isTop) { // 초: 원정팀 공격
			return this.awayBattingOrder;
		} else { // 말: 홈팀 공격
			return this.homeBattingOrder;
		}
	}

	// 현재 수비 팀의 선발 투수를 반환하는 헬퍼 메서드
	public Pitcher getCurrentDefensivePitcher() {
		if (this.isTop) { // 초: 홈팀 수비
			return this.homeStartingPitcher;
		} else { // 말: 원정팀 수비
			return this.awayStartingPitcher;
		}
	}

	// 게임 상태 검증 메서드
	public boolean isValidGameState() {
		return inning >= 1 && inning <= maxInning &&
				out >= 0 && out <= MAX_OUTS &&
				strike >= 0 && strike <= MAX_STRIKES &&
				ball >= 0 && ball <= MAX_BALLS;
	}

	// 이닝 종료 여부 확인
	public boolean isInningOver() {
		return out >= MAX_OUTS;
	}

	// 게임 종료 조건 확인
	public boolean isGameEndCondition() {
		return inning >= maxInning && !isTop && isInningOver();
	}
}