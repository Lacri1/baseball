package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Batter extends Player {
    // 순수 실제 성적만 유지
    private int atBats; // 타수
    private int hits; // 안타
    private int homeRuns; // 홈런
    private int rbis; // 타점
    private double battingAverage; // 타율
    private int plateAppearances; // 타석 (타율/홈런율 계산에 활용)
    private int strikeOuts; // 삼진 (컨택 관련)
    private int walks; // 볼넷 (선구안 관련, 필요한 경우)
    private int twoBases; // 2루타 (장타력 관련)
    private int threeBases; // 3루타 (장타력 관련)
    private int hitByPitch;

    public Batter(String name, String team) {
        this.setName(name);
        this.setTeam(team);
    }

    // 타율 계산 메서드
    public double calculateBattingAverage() {
        return atBats > 0 ? (double) hits / atBats : 0.0;
    }

    // 출루율 계산 메서드
    public double calculateOnBasePercentage() {
        if (plateAppearances == 0)
            return 0.0;
        return (double) (hits + walks + hitByPitch) / plateAppearances;
    }

    // 장타율 계산 메서드
    public double calculateSluggingPercentage() {
        if (atBats == 0)
            return 0.0;
        int totalBases = hits + (twoBases * 2) + (threeBases * 3) + (homeRuns * 4);
        return (double) totalBases / atBats;
    }

    // OPS 계산 메서드
    public double calculateOPS() {
        return calculateOnBasePercentage() + calculateSluggingPercentage();
    }
}