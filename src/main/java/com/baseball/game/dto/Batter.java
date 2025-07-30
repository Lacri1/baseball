package com.baseball.game.dto;

import lombok.Data;

@Data
public class Batter extends Player {
	private int position; // 타순
	private int atBats; // 타수
	private int hits; // 안타
	private int homeRuns; // 홈런
	private int rbis; // 타점
	private double battingAverage; // 타율

	// --- 새롭게 추가될 필드들 ---
	private int plateAppearances; // 타석 (Plate_Appearance)
	private int strikeOuts; // 삼진 (Strike_Out)
	private int fourBalls; // 볼넷 (Four_Ball)
	private int twoBases; // 2루타 (two_Base)
	private int threeBases; // 3루타 (three_Base)
	// --- 추가 필드 끝 ---

	public Batter() {
		this.atBats = 0;
		this.hits = 0;
		this.homeRuns = 0;
		this.rbis = 0;
		this.battingAverage = 0.0;
		// --- 새롭게 추가된 필드들 초기화 ---
		this.plateAppearances = 0;
		this.strikeOuts = 0;
		this.fourBalls = 0;
		this.twoBases = 0;
		this.threeBases = 0;
		// --- 초기화 끝 ---
	}
	public Batter(String name,String team) {
		this.getName();
		this.getTeam();
	}
}