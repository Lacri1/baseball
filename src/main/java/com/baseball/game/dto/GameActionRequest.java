package com.baseball.game.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameActionRequest {
    @NotNull
    private String pitchType;
    @NotNull
    private Boolean swing;
    // 사용자가 "타이밍 보너스"를 사용할지 여부
    private Double timing;
}