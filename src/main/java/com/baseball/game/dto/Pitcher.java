package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pitcher extends Player {
    // 순수 실제 성적만 유지
    private int inningsPitched; // 이닝 수
    private int strikeouts; // 삼진
    private int walks; // 볼넷
    private int hits; // 피안타
    private int earnedRuns; // 자책점
    private double era; // 평균자책점
    private double whip; // WHIP (Walks plus Hits per Inning Pitched)
    private int pitchersBattersFaced; // 상대 타자 수
    private int hitByPitch;

    public Pitcher(String name, String team) {
        this.setName(name);
        this.setTeam(team);
    }

    // ERA 계산 메서드
    public double calculateERA() {
        return inningsPitched > 0 ? (earnedRuns * 9.0) / inningsPitched : 0.0;
    }

    // WHIP 계산 메서드
    public double calculateWHIP() {
        return inningsPitched > 0 ? (double) (walks + hits) / inningsPitched : 0.0;
    }

    // 삼진율 계산 메서드
    public double calculateStrikeoutRate() {
        return pitchersBattersFaced > 0 ? (double) strikeouts / pitchersBattersFaced : 0.0;
    }

    // 볼넷율 계산 메서드
    public double calculateWalkRate() {
        return pitchersBattersFaced > 0 ? (double) walks / pitchersBattersFaced : 0.0;
    }

    // 피안타율 계산 메서드
    public double calculateHitRate() {
        return pitchersBattersFaced > 0 ? (double) hits / pitchersBattersFaced : 0.0;
    }
}