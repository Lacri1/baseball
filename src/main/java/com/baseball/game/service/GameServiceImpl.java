package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.mapper.BatterMapper;
import com.baseball.game.mapper.PitcherMapper;

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
}