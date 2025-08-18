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
    private Double timing;
}