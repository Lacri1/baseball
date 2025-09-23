package com.baseball.game.controller;

import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.service.TeamLineupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/kbo")
@CrossOrigin(origins = "http://localhost:3000")
public class KboStatsController {

    @Autowired
    private TeamLineupService teamLineupService;

    @GetMapping("/hitter-stats")
    public List<Batter> getAllHitterStats(@RequestParam(required = false) String sortBy) {
        return teamLineupService.getAllBattersWithStats(sortBy);
    }

    @GetMapping("/pitcher-stats")
    public List<Pitcher> getAllPitcherStats(@RequestParam(required = false) String sortBy) {
        return teamLineupService.getAllPitchersWithStats(sortBy);
    }

    @GetMapping("/team-stats")
    public List<com.baseball.game.dto.TeamStats> getAllTeamStats() {
        return teamLineupService.getAllTeamStats();
    }
}