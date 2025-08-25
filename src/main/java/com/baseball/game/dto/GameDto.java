package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.baseball.game.constant.GameConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameDto {
	private String gameId;
	private String userId; // 게임을 시작한 사용자 ID
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
	private int homeHit;
	private int awayHit;
	private int homeWalks;
	private int awayWalks;
	private Batter currentBatter; // 현재 타석에 있는 타자
	private Pitcher currentPitcher; // 현재 마운드에 있는 투수
	private Batter[] bases; // 0: 홈, 1: 1루, 2: 2루, 3: 3루
	private boolean gameOver;
	private String winner;

	// 새로 추가: 홈팀 및 원정팀의 타순 (Batter 객체 리스트)
	private List<Batter> homeBattingOrder;
	private List<Batter> awayBattingOrder;

	// 새로 추가: 홈팀 및 원정팀의 선발 투수 (Pitcher 객체)
	private Pitcher homeStartingPitcher;
	private Pitcher awayStartingPitcher;

	// 기존 필드 유지
	private boolean isUserOffense; // 사용자가 공격 팀인지 여부 (초기 게임 생성 시 설정, 사용자의 팀이 어느 팀인지 저장)

	private int currentBatterIndex; // 현재 타순 인덱스 (현재 공격 팀의 라인업 기준)
	// 팀별 타순 인덱스(야구 규칙: 타순은 이닝이 넘어가도 팀별로 이어짐)
	private int homeBatterIndex;
	private int awayBatterIndex;

	// 타석/경기 이벤트 로그 (최신순으로 append)
	private List<PlayEvent> eventLog;

	// 이번 경기 타자/투수 스탯 분리 맵
	private Map<String, BatterGameStats> batterGameStatsMap;
	private Map<String, PitcherGameStats> pitcherGameStatsMap;

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
		this.bases = new Batter[GameConstants.MAX_BASES]; // 홈, 1루, 2루, 3루
		this.inning = 1;
		this.isTop = true; // 기본값: 1회 초 (원정팀 공격)
		this.out = 0;
		this.strike = 0;
		this.ball = 0;
		this.homeScore = 0;
		this.awayScore = 0;
		this.homeHit = 0;
		this.awayHit = 0;
		this.homeWalks = 0;
		this.awayWalks = 0;
		this.gameOver = false;
		this.homeBattingOrder = new ArrayList<>(); // 초기화
		this.awayBattingOrder = new ArrayList<>(); // 초기화
		this.currentBatterIndex = 0;
		this.homeBatterIndex = 0;
		this.awayBatterIndex = 0;
		this.eventLog = new ArrayList<>();
		this.batterGameStatsMap = new java.util.HashMap<>();
		this.pitcherGameStatsMap = new java.util.HashMap<>();
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
				out >= 0 && out <= GameConstants.MAX_OUTS &&
				strike >= 0 && strike <= GameConstants.MAX_STRIKES &&
				ball >= 0 && ball <= GameConstants.MAX_BALLS;
	}

	// 이닝 종료 여부 확인
	public boolean isInningOver() {
		return out >= GameConstants.MAX_OUTS;
	}

	// 게임 종료 조건 확인
	public boolean isGameEndCondition() {
		return inning >= maxInning && !isTop && isInningOver();
	}

	// 응답 편의 필드: 현재 공격/수비 팀 및 초/말 표시
	@JsonProperty("offenseTeam")
	public String getOffenseTeam() {
		return isTop ? awayTeam : homeTeam;
	}

	@JsonProperty("defenseTeam")
	public String getDefenseTeam() {
		return isTop ? homeTeam : awayTeam;
	}

	@JsonProperty("offenseSide")
	public String getOffenseSide() {
		return isTop ? "TOP" : "BOTTOM";
	}

	// 보기 편의: 팀/타순 기준 이번 경기 타자 스탯 배열 제공 (맵은 유지)
	@JsonProperty("homeBatterStats")
	public java.util.List<BatterGameStats> getHomeBatterStats() {
		java.util.List<BatterGameStats> list = new java.util.ArrayList<>();
		if (this.homeBattingOrder == null)
			return list;
		for (Batter b : this.homeBattingOrder) {
			if (b == null || b.getName() == null)
				continue;
			String name = b.getName();
			BatterGameStats s = (this.batterGameStatsMap != null) ? this.batterGameStatsMap.get(name) : null;
			if (s == null) {
				s = BatterGameStats.builder().playerName(name).build();
			}
			list.add(s);
		}
		return list;
	}

	@JsonProperty("awayBatterStats")
	public java.util.List<BatterGameStats> getAwayBatterStats() {
		java.util.List<BatterGameStats> list = new java.util.ArrayList<>();
		if (this.awayBattingOrder == null)
			return list;
		for (Batter b : this.awayBattingOrder) {
			if (b == null || b.getName() == null)
				continue;
			String name = b.getName();
			BatterGameStats s = (this.batterGameStatsMap != null) ? this.batterGameStatsMap.get(name) : null;
			if (s == null) {
				s = BatterGameStats.builder().playerName(name).build();
			}
			list.add(s);
		}
		return list;
	}
}