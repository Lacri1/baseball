package com.baseball.game.service;

import java.util.List;

import com.baseball.game.dto.Player;

public interface TeamService {
    List<Player> getPlayersByTeam(String teamId);
    void registerLineup(String gameId, List<String> battingOrder, String StartPitcher);
}