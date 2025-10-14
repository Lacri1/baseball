package com.baseball.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamLineupSetRequest {
    private String teamName;
    private List<String> battingOrder; // 타자 이름 목록 (타순대로)
    private String startingPitcher; // 선발 투수 이름
    private List<String> battingOrderIds; // 타자 ID 목록
    private String pitcherId; // 투수 ID
}