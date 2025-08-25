package com.baseball.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameActionRequest {
    private String pitchType;
    private Boolean swing;
    // 사용자가 "타이밍 보너스"를 사용할지 여부
    private Boolean timing;
}