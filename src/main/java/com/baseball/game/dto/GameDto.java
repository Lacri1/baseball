package com.baseball.game.dto;

import lombok.Data;
import java.util.List;
import java.util.ArrayList;

@Data
public class GameDto {
	private String gameId;
	private String homeTeam;
	private String awayTeam;
	private int inning;
	private boolean isTop; // true: 초, false: 말
	private int out;
	private int strike;
	private int ball;
	private int homeScore;
	private int awayScore;
	private List<Batter> baseRunners; // 베이스 러너들
	private Batter currentBatter;
	private Pitcher currentPitcher;
	private Batter[] bases; // 0: 홈, 1: 1루, 2: 2루, 3: 3루
	private boolean gameOver;
	private String winner;
	private List<Batter> battingOrder; // 타순
	private List<Pitcher> pitcherList; // 팀 투수 전체
	private Pitcher startingPitcher; // 선발투수
	private boolean IsUserOffense;
	private int maxInning; // 설정 이닝 수
	private int currentBatterIndex; // 현재 타순 인덱스

	public GameDto() {
		this.baseRunners = new ArrayList<>();
		this.bases = new Batter[4]; // 홈, 1루, 2루, 3루
		this.inning = 1;
		this.isTop = true;
		this.out = 0;
		this.strike = 0;
		this.ball = 0;
		this.homeScore = 0;
		this.awayScore = 0;
		this.gameOver = false;
		this.IsUserOffense = true; // 유저가 먼저 공격
		this.battingOrder = new ArrayList<>();
		this.pitcherList = new ArrayList<>();
		this.maxInning = 9; // 기본값 9이닝
		this.currentBatterIndex = 0;
	}
}
