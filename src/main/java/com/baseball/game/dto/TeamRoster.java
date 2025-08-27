package com.baseball.game.dto;

import java.util.List;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamRoster {
    private String teamId;
    private List<Batter> batters;
    private List<Pitcher> pitchers;
}
