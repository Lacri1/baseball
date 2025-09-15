package com.baseball.game.controller;

import com.baseball.game.dto.Player;
import com.baseball.game.dto.TeamRoster;
import com.baseball.game.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/team")
@CrossOrigin(origins = "http://localhost:3000")
public class TeamController {

    private final TeamService service;

    @Autowired
    public TeamController(TeamService service) {
        this.service = service;
    }

    private static boolean containsHangul(String s) {
        if (s == null)
            return false;
        for (int i = 0; i < s.length(); i++) {
            char ch = s.charAt(i);
            if ((ch >= 0xAC00 && ch <= 0xD7A3) || (ch >= 0x1100 && ch <= 0x11FF)) {
                return true;
            }
        }
        return false;
    }

    private static String normalizeKoreanPathVariable(String input) {
        if (input == null)
            return null;
        if (containsHangul(input)) {
            return input;
        }
        String recovered = new String(input.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return containsHangul(recovered) ? recovered : input;
    }

    @GetMapping("/list")
    public List<String> getTeamList() {
        return service.getTeamList();
    }

    /**
     * 팀별 선수(라인업 후보) 목록 조회
     * 
     * @param teamId 팀 ID
     * @return 해당 팀의 선수 목록
     */
    @GetMapping("/{teamId}/players")
    public List<Player> getPlayersByTeam(@PathVariable String teamId) {
        String normalizedTeamId = normalizeKoreanPathVariable(teamId);
        return service.getPlayersByTeam(normalizedTeamId);
    }

    /**
     * 팀별 로스터(타자+투수) 조회
     */
    @GetMapping("/{teamId}/roster")
    public TeamRoster getRosterByTeam(@PathVariable String teamId) {
        String normalizedTeamId = normalizeKoreanPathVariable(teamId);
        return service.getRosterByTeam(normalizedTeamId);
    }

    // 기존 registerLineup 메서드는 GameController의 setTeamLineupAndPitcher와 중복되므로 제거합니다.
    // 게임별 라인업 설정은 GameService에서 담당하는 것이 더 적절합니다.
}