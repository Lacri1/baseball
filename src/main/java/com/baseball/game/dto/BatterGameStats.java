package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatterGameStats {
    private String playerName;
    private int plateAppearances; // 타석
    private int atBats; // 타수
    private int hits; // 안타
    private int homeRuns; // 홈런
    private int walks; // 볼넷
    private int strikeouts; // 삼진(당한)
    private int rbis; // 타점

    public double battingAverage() {
        return atBats > 0 ? (double) hits / atBats : 0.0;
    }
}
