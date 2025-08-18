package com.baseball.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameCreateRequest {
    private String UserId;
    private String homeTeam;
    private String awayTeam;
    private boolean IsUserOffense;
    private int maxInning; // 사용자가 선택한 이닝 수
    private boolean enableTiming; // 타이밍 시스템 활성화 여부

    public GameCreateRequest(String homeTeam, String awayTeam, int maxInning, boolean enableTiming) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.maxInning = maxInning;
        this.enableTiming = enableTiming;
    }

    public String getUserId() {
        return UserId;
    }
}