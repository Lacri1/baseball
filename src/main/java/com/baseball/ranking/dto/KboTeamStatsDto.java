package com.baseball.ranking.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KboTeamStatsDto {
    private Long no;
    private String teamName; // 팀이름
    private Integer gameNum; // 총 경기수
    private Integer win; // 승
    private Integer lose; // 패
    private Integer draw; // 무
    private Double winPercentage; // 승률 
    private Double gamesBehind; // 게임차
}