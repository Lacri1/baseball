package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreboardDto {
    private String gameId;
    private String awayTeam;
    private String homeTeam;
    

    private int currentInning; // 현재 진행 이닝(1-base)
    private int maxInning; // 규정 이닝 수
    private boolean isTop; // true: 초, false: 말

    // 이닝별 득점
    private List<Integer> awayByInning; // size = maxInning
    private List<Integer> homeByInning; // size = maxInning
   

    // 합계
    private int awayScore;
    private int homeScore;
    private int awayHit;
    private int homeHit;
    private int awayWalks;
    private int homeWalks;
}
