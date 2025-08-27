package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayEvent {
    // 이벤트 유형: PA_END(타석 종료), GAME_END(경기 종료)
    private String type;

    // 이닝/초말 정보
    private int inning;
    private boolean isTop;

    // 타자/투수/공격팀 표시용
    private String offenseTeam;
    private String batter;
    private String pitcher;

    // 결과 요약
    private String result; // 예: "안타", "2루타", "삼진 아웃", "볼넷", "병살타" 등
    private String description; // 프론트 표시용 간단 문구

    // 카운트/아웃/스코어 스냅샷 (후 상태)
    private int out;
    private int strike;
    private int ball;
    private int homeScore;
    private int awayScore;
    private int homeHit;
    private int awayHit;
    private int homeWalks;
    private int awayWalks;
}
