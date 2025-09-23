package com.baseball.game.dto;

import lombok.Data;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
    private List<Batter> homeBattingOrder;
    private List<Batter> awayBattingOrder;
    private List<Pitcher> pitcherList; // 팀 투수 전체
    private Pitcher startingPitcher; // 선발투수
    private Pitcher homeStartingPitcher;
    private Pitcher awayStartingPitcher;
    private boolean userOffense;
    private int maxInning; // 설정 이닝 수
    private int currentBatterIndex; // 현재 타순 인덱스
    private int homeBatterIndex;
    private int awayBatterIndex;
    private List<PlayEvent> eventLog;
    private Map<String, BatterGameStats> batterGameStatsMap;
    private Map<String, PitcherGameStats> pitcherGameStatsMap;
    private String userId;
    private int homeHit;
    private int awayHit;
    private int homeWalks;
    private int awayWalks;


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
        this.userOffense = true; // 유저가 먼저 공격
        this.homeBattingOrder = new ArrayList<>();
        this.awayBattingOrder = new ArrayList<>();
        this.pitcherList = new ArrayList<>();
        this.maxInning = 9; // 기본값 9이닝
        this.currentBatterIndex = 0;
        this.homeBatterIndex = 0;
        this.awayBatterIndex = 0;
        this.eventLog = new ArrayList<>();
        this.batterGameStatsMap = new HashMap<>();
        this.pitcherGameStatsMap = new HashMap<>();
        this.homeHit = 0;
        this.awayHit = 0;
        this.homeWalks = 0;
        this.awayWalks = 0;
    }

    public List<Batter> getCurrentOffensiveLineup() {
        return isTop ? awayBattingOrder : homeBattingOrder;
    }

    public Pitcher getCurrentDefensivePitcher() {
        // For now, let's assume the starting pitcher plays the whole game.
        // This can be expanded later to support pitcher changes.
        return startingPitcher;
    }

    public String getOffenseTeam() {
        return isTop ? awayTeam : homeTeam;
    }

    public String getDefenseTeam() {
        return isTop ? homeTeam : awayTeam;
    }

    public String getOffenseSide() {
        return isTop ? "TOP" : "BOTTOM";
    }
}