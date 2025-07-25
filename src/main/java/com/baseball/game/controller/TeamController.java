package com.baseball.game.controller;

import com.baseball.game.dto.Player;
import com.baseball.game.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import lombok.Setter;
import java.util.List;

@RestController
@RequestMapping("/api/team")
public class TeamController {
    
    private final TeamService service;
    
    @Autowired
    public TeamController(TeamService service) {
    	this.service=service;
    }

    // 팀별 선수(라인업 후보) 목록 조회
    @GetMapping("/{teamId}/players")
    public List<Player> getPlayersByTeam(@PathVariable String teamId) {
        return service.getPlayersByTeam(teamId);
    }

    @PostMapping("/{gameId}/lineup")
    public void registerLineup(@PathVariable String gameId, @RequestBody List<String> battingOrder,
            @RequestBody String StartPitcher) {
        service.registerLineup(gameId, battingOrder, StartPitcher);
    }
}
