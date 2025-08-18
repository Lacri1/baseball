package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GamePlayView {
    private String gameId;
    private String userId;
    private String homeTeam;
    private String awayTeam;
    private int inning;
    private boolean isTop;
    private String offenseTeam;
    private String defenseTeam;
    private String offenseSide;
    private int out;
    private int strike;
    private int ball;
    private int homeScore;
    private int awayScore;
    private Batter currentBatter;
    private Pitcher currentPitcher;
    // 주자/베이스 상태
    private java.util.List<Batter> baseRunners;
    private Batter[] bases; // index 0: 1루, 1: 2루, 2: 3루 (홈은 제외)
}
