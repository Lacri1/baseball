package com.baseball.game.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baseball.game.dto.TeamLineup;
import com.baseball.game.dto.CustomLineupRequest;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.dto.TeamStats;
import com.baseball.game.mapper.TeamLineupMapper;
import lombok.Setter;

import com.baseball.game.exception.ValidationException;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

@Service
public class TeamLineupServiceImpl implements TeamLineupService {

    @Setter(onMethod_ = @Autowired)
    private TeamLineupMapper teamLineupMapper;

    @Override
    public List<TeamLineup> getDefaultLineup(String teamName) {
        return teamLineupMapper.findDefaultLineupByTeam(teamName);
    }

    @Override
    public List<TeamLineup> getCustomLineup(String userId, String teamName) {
        return teamLineupMapper.findCustomLineupByUserAndTeam(userId, teamName);
    }

    @Override
    @Transactional
    public void saveCustomLineup(CustomLineupRequest request) {
        if (request.getLineup() == null || request.getLineup().size() != 10) {
            throw new ValidationException("라인업은 정확히 10명(타자 9명, 투수 1명)으로 구성되어야 합니다.");
        }

        // 기존 커스텀 라인업 삭제
        teamLineupMapper.deleteCustomLineupByUserAndTeam(request.getUserId(), request.getTeamName());

        // 새로운 커스텀 라인업 저장
        for (CustomLineupRequest.LineupPosition position : request.getLineup()) {
            TeamLineup lineup = new TeamLineup();
            lineup.setTeamName(request.getTeamName());
            lineup.setUserId(request.getUserId());
            lineup.setPosition(position.getPosition() != null ? String.valueOf(position.getPosition()) : "P");
            lineup.setPlayerName(position.getPlayerName());
            lineup.setPlayerId(String.valueOf(position.getPlayerId()));

            teamLineupMapper.insertCustomLineup(lineup);
        }
    }

    @Override
    public List<TeamLineup> getAllCustomLineups(String userId) {
        return teamLineupMapper.findAllCustomLineupsByUser(userId);
    }

    @Override
    public List<String> getAvailablePlayers(String teamName) {
        return teamLineupMapper.findAvailablePlayersByTeam(teamName);
    }

    @Override
    public List<String> getAvailablePitchers(String teamName) {
        // TODO: Implement this method to return only pitchers
        return teamLineupMapper.findAvailablePlayersByTeam(teamName);
    }

    @Override
    public List<Batter> getAllBattersWithStats(String sortBy) {
        List<Batter> allBatters = teamLineupMapper.findAllBatters(sortBy);

        if ("battingAverage".equals(sortBy)) {
            List<TeamStats> teamStats = teamLineupMapper.findAllTeamStats();
            Map<String, Integer> teamGameNumMap = teamStats.stream()
                    .collect(Collectors.toMap(TeamStats::getTeamName, TeamStats::getGameNum));

            return allBatters.stream()
                    .filter(batter -> {
                        Integer gameNum = teamGameNumMap.get(batter.getTeam());
                        return gameNum != null && batter.getPlateAppearances() >= gameNum * 3.1;
                    })
                    .collect(Collectors.toList());
        }
        return allBatters;
    }

    @Override
    public List<Pitcher> getAllPitchersWithStats(String sortBy) {
        List<Pitcher> allPitchers = teamLineupMapper.findAllPitchers(sortBy);

        if ("era".equals(sortBy)) {
            List<TeamStats> teamStats = teamLineupMapper.findAllTeamStats();
            Map<String, Integer> teamGameNumMap = teamStats.stream()
                    .collect(Collectors.toMap(TeamStats::getTeamName, TeamStats::getGameNum));

            return allPitchers.stream()
                    .filter(pitcher -> {
                        Integer gameNum = teamGameNumMap.get(pitcher.getTeam());
                        // Convert inningsPitched to a comparable format, e.g., total outs
                        double inningsPitched = pitcher.getInningsPitched();
                        return gameNum != null && inningsPitched >= gameNum * 1.0;
                    })
                    .collect(Collectors.toList());
        }
        return allPitchers;
    }

    @Override
    public List<TeamStats> getAllTeamStats() {
        return teamLineupMapper.findAllTeamStats();
    }
}