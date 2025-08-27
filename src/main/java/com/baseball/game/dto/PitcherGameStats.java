package com.baseball.game.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PitcherGameStats {
    private String playerName;
    private int strikeouts; // 탈삼진
    private int walks; // 볼넷허용
    private int hitsAllowed; // 피안타
    private int homersAllowed; // 피홈런
    private int pitches; // 투구 수
    private int outsRecorded; // 기록한 아웃 수 (3 아웃 = 1이닝)
    private int earnedRunsAllowed; // 실점(자책)

    public String getInningsPitchedDisplay() {
        int outs = outsRecorded;
        int full = outs / 3;
        int rem = outs % 3;
        return full + "." + rem;
    }
}
