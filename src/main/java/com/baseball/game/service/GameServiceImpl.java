package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    @Autowired
    private GameLifecycleService lifecycleService;
    @Autowired
    private GameStateService stateService;
    @Autowired
    private GameActionService actionService;

    @Override
    public GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense) {
        return lifecycleService.createGame(homeTeam, awayTeam, maxInning, isUserOffense);
    }

    @Override
    public GameDto getGame(String gameId) {
        return lifecycleService.getGame(gameId);
    }

    @Override
    public void resetGame(String gameId) {
        lifecycleService.resetGame(gameId);
    }

    @Override
    public String batterSwing(String gameId, boolean swing) {
        return actionService.batterSwing(gameId, swing, 0.5); // Default timing
    }

    @Override
    public String batterSwing(String gameId, boolean swing, double timing) {
        return actionService.batterSwing(gameId, swing, timing);
    }

    @Override
    public String pitcherThrow(String gameId, String pitchType) {
        return actionService.pitcherThrow(gameId, pitchType);
    }

    @Override
    public String playComputerTurn(String gameId) {
        return actionService.playComputerTurn(gameId);
    }

    @Override
    public GameDto nextInning(String gameId) {
        return stateService.nextInning(gameId);
    }

    @Override
    public GameDto endGame(String gameId) {
        return stateService.endGame(gameId);
    }

    @Override
    public void advanceRunners(String gameId, int bases) {
        stateService.advanceRunners(gameId, bases);
    }

    @Override
    public String getGameStats(String gameId) {
        GameDto game = lifecycleService.getGame(gameId);
        StringBuilder stats = new StringBuilder();
        stats.append("=== 게임 통계 ===\n");
        stats.append(String.format("홈팀: %s (%d점)\n", game.getHomeTeam(), game.getHomeScore()));
        stats.append(String.format("원정팀: %s (%d점)\n", game.getAwayTeam(), game.getAwayScore()));
        stats.append(String.format("이닝: %d%s\n", game.getInning(), game.isTop() ? "초" : "말"));
        stats.append(String.format("아웃: %d, 스트라이크: %d, 볼: %d\n", game.getOut(), game.getStrike(), game.getBall()));
        if (game.isGameOver()) {
            stats.append(String.format("게임 종료! 승자: %s\n", game.getWinner()));
        }
        return stats.toString();
    }

    @Override
    public void setLineup(String gameId, com.baseball.game.dto.LineupRequest request) {
        lifecycleService.updateLineup(gameId, request.getTeamName(), request);
    }
}