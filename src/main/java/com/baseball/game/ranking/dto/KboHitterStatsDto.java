package com.baseball.game.ranking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KboHitterStatsDto {
    private Long no;
    private String playerName; // 선수 이름
    private String playerTeam; // 선수 팀 이름
    private Double battingAverage; // 타율
    private Integer gameNum; // 경기 수 
    private Integer plateAppearance; // 타석
    private Integer run; // 득점
    private Integer hit; // 안타
    private Integer twoBase; // 2루타
    private Integer threeBase; // 3루타
    private Integer homeRun; // 홈런
    private Integer runsBattedIn; // 타점
    private Integer fourBall; // 볼넷
    private Integer strikeOut; // 삼진
    private Double onBasePercentage; // 출루율
    private Double onbasePlusSlug; // OPS 
}