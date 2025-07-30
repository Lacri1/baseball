package com.baseball.game.dto;

import lombok.Data;

@Data
public class Pitcher extends Player {
	private int inningsPitched; // 이닝 수
	private int strikeouts; // 삼진
	private int walks; // 볼넷 (Base_On_Balls와 동일하게 사용 가능)
	private int hits; // 피안타
	private int homeruns;
	private int earnedRuns; // 자책점
	private double era; // 평균자책점

	// --- 새롭게 추가될 필드들 ---
	private int totalBattersFaced; // 상대 타자 수 (Total_Batters_Faced)
	// --- 추가 필드 끝 ---

	public Pitcher() {
		this.inningsPitched = 0;
		this.strikeouts = 0;
		this.walks = 0;
		this.hits = 0;
		this.earnedRuns = 0;
		this.era = 0.0;
		// --- 새롭게 추가된 필드들 초기화 ---
		this.totalBattersFaced = 0;
		// --- 초기화 끝 ---
	}
	public Pitcher(String name,String team) {
		this.getName();
		this.getTeam();
	}
}