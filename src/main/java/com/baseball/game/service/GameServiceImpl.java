package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.mapper.BatterMapper;
import com.baseball.game.mapper.PitcherMapper;
import com.baseball.game.dto.TeamLineupSetRequest;
import com.baseball.game.util.ValidationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Setter;

@Service
@Transactional
public class GameServiceImpl implements GameService {
    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    @Autowired
    private GameLifecycleService lifecycleService;
    
    @Autowired
    private GameStateService stateService;
    
    @Autowired
    private GameActionService actionService;
    
    @Autowired
    private GameValidationService validationService;

    @Setter(onMethod_ = @Autowired)
    private BatterMapper batterMapper;

    @Setter(onMethod_ = @Autowired)
    private PitcherMapper pitcherMapper;

    // 게임 데이터는 GameLifecycleServiceImpl에서 관리됩니다.

    @Override
    @Transactional
    public GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense) {
        return lifecycleService.createGame(homeTeam, awayTeam, maxInning, isUserOffense);
    }

    @Override
    public GameDto getGame(String gameId) {
        return lifecycleService.getGame(gameId);
    }

    @Override
    @Transactional
    public String batterSwing(String gameId, Boolean swing, Double timing) {
        return actionService.batterSwing(gameId, swing, timing);
    }

 // GameServiceImpl.java 파일의 pitcherThrow 메서드를 아래와 같이 수정합니다.

    @Override
    @Transactional
    public String pitcherThrow(String gameId, String pitchType) {
        return actionService.pitcherThrow(gameId, pitchType);
    }
 // GameServiceImpl에 추가할 AI 제어 메서드 (예시)

    @Override
    public String playComputerTurn(String gameId) {
        return actionService.playComputerTurn(gameId);
    }

    // 이 메서드 외에 다음 메서드는 GameLogicUtil.java에 이미 존재합니다.
    // private static int calculateContactFromBattingAverage(double battingAverage) { ... }

    @Override
    @Transactional
    public GameDto nextInning(String gameId) {
        return stateService.nextInning(gameId);
    }

    @Override
    @Transactional
    public GameDto endGame(String gameId) {
        return stateService.endGame(gameId);
    }

    @Override
    @Transactional
    public void advanceRunners(String gameId, Integer basesToAdvance) {
        stateService.advanceRunners(gameId, basesToAdvance);
    }

    // 위임 패턴 적용으로 인해 더 이상 필요하지 않은 메서드들
    // 이 메서드들은 각각의 세분화된 서비스로 이동되었습니다.

    @Override
    public void resetGame(String gameId) {
        lifecycleService.resetGame(gameId);
    }

    @Override
    public GameDto applyTeamLineup(String gameId, TeamLineupSetRequest request) {
        ValidationUtil.validateGameId(gameId);
        ValidationUtil.validateTeamLineupSetRequest(request);

        GameDto game = lifecycleService.getGame(gameId);
        boolean isHome = request.getTeamName().equals(game.getHomeTeam());
        boolean isAway = request.getTeamName().equals(game.getAwayTeam());
        if (!isHome && !isAway) {
            throw new com.baseball.game.exception.ValidationException("요청 팀이 게임의 홈/원정팀과 일치하지 않습니다.");
        }

        // 타자 9명 로딩 및 선발 투수 로딩
        java.util.List<com.baseball.game.dto.Batter> batters = batterMapper.findByNames(request.getBattingOrder());
        java.util.Map<String, com.baseball.game.dto.Batter> byName = new java.util.HashMap<>();
        if (batters != null) {
            for (com.baseball.game.dto.Batter b : batters) {
                if (b != null && b.getName() != null) {
                    if (b.getAtBats() > 0 && b.getBattingAverage() == 0.0) {
                        b.setBattingAverage(b.calculateBattingAverage());
                    }
                    byName.put(b.getName(), b);
                }
            }
        }
        java.util.List<com.baseball.game.dto.Batter> ordered = new java.util.ArrayList<>();
        for (String n : request.getBattingOrder()) {
            com.baseball.game.dto.Batter b = byName.get(n);
            if (b == null) {
                throw new com.baseball.game.exception.ValidationException("타자를 찾을 수 없습니다: " + n);
            }
            ordered.add(b);
        }

        com.baseball.game.dto.Pitcher sp = pitcherMapper.findByName(request.getStartingPitcher());
        if (sp == null) {
            throw new com.baseball.game.exception.ValidationException("선발 투수를 찾을 수 없습니다: " + request.getStartingPitcher());
        }

        if (isHome) {
            game.setHomeBattingOrder(ordered);
            game.setHomeStartingPitcher(sp);
            if (!game.isTop()) {
                // 말(홈 공격)에서만 현재 타자/투수 영향
                game.setCurrentBatterIndex(0);
                if (!ordered.isEmpty()) game.setCurrentBatter(ordered.get(0));
                game.setCurrentPitcher(game.getAwayStartingPitcher());
            }
        } else {
            game.setAwayBattingOrder(ordered);
            game.setAwayStartingPitcher(sp);
            if (game.isTop()) {
                // 초(원정 공격)에서만 현재 타자/투수 영향
                game.setCurrentBatterIndex(0);
                if (!ordered.isEmpty()) game.setCurrentBatter(ordered.get(0));
                game.setCurrentPitcher(game.getHomeStartingPitcher());
            }
        }

        return game;
    }
}