package com.baseball.game.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamLineup {
    private int id;
    private String teamName;
    private String userId;
    private String position; // 타순 (1~9)
    private String playerName;
    private String playerId;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}