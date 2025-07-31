package com.baseball.game.dto;

import lombok.Data;

@Data
public class Batter extends Player {
    // 순수 실제 성적만 유지
    private int at_Bats; // 타수
    private int hits; // 안타
    private int home_Runs; // 홈런
    private int rbis; // 타점
    private double battingAverage; // 타율
    private int plate_Appearances; // 타석 (타율/홈런율 계산에 활용)
    private int strikeOuts; // 삼진 (컨택 관련)
    private int four_Balls; // 볼넷 (선구안 관련, 필요한 경우)
    private int two_Bases; // 2루타 (장타력 관련)
    private int three_Bases; // 3루타 (장타력 관련)

    public Batter(String name,String team) {
        this.setName(name);
        this.setTeam(team);
    }
}