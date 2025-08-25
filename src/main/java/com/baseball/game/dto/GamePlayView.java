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
    private int homeHit;
    private int awayHit;
    private int homeWalks;
    private int awayWalks;
    private Batter currentBatter;
    private Pitcher currentPitcher;
    // 주자/베이스 상태 (GameDto와 동일하게 1~3 인덱스 사용)
    private Batter[] bases;
    // 타석/경기 이벤트 로그
    private List<PlayEvent> eventLog;
    // 현재 타석 종료 시 표시용 이번 경기 개별 스탯(타자/투수 최소치)
    private BatterGameStats batterGameStats;
    private PitcherGameStats pitcherGameStats;
}
