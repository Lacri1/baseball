package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.exception.InvalidGameStateException;
import com.baseball.game.util.GameLogicUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class GameActionServiceImpl implements GameActionService {

    private static final Logger log = LoggerFactory.getLogger(GameActionServiceImpl.class);

    @Autowired
    private GameLifecycleService lifecycleService;

    @Override
    @Transactional
    public String batterSwing(String gameId, Boolean swing, Double timing) {
        GameDto game = lifecycleService.getGame(gameId);

        if (game.isGameOver()) {
            throw new InvalidGameStateException("게임이 이미 종료되었습니다.");
        }
        if (game.getCurrentBatter() == null || game.getCurrentPitcher() == null) {
            throw new InvalidGameStateException("현재 타자 또는 투수가 설정되지 않았습니다. 라인업을 먼저 설정해주세요.");
        }
        if (game.getOut() >= 3 && game.getStrike() == 0 && game.getBall() == 0) {
            throw new InvalidGameStateException("현재 공격 이닝이 종료되었습니다. 다음 이닝으로 진행해주세요.");
        }

        String actualPitchType = "strike";

        String hitResult = GameLogicUtil.determineHitResultWithTiming(
                swing,
                game.getCurrentPitcher(),
                actualPitchType,
                timing,
                game.getCurrentBatter());

        log.info("게임 {}: 타자 {} (타이밍: {}) 스윙: {}, 투수 {} 투구 결과: {}, 타격 결과: {}",
                gameId, game.getCurrentBatter().getName(), timing, swing, game.getCurrentPitcher().getName(),
                actualPitchType, hitResult);

        // 결과에 따른 게임 상태 업데이트
        switch (hitResult) {
            case "스트라이크":
                game.setStrike(game.getStrike() + 1);
                break;
            case "볼":
                game.setBall(game.getBall() + 1);
                break;
            case "헛스윙":
                game.setStrike(game.getStrike() + 1);
                break;
            case "안타":
            case "2루타":
            case "3루타":
                int basesToAdvance = 0;
                if (hitResult.equals("안타"))
                    basesToAdvance = 1;
                else if (hitResult.equals("2루타"))
                    basesToAdvance = 2;
                else if (hitResult.equals("3루타"))
                    basesToAdvance = 3;

                GameLogicUtil.advanceRunners(game, basesToAdvance);
                GameLogicUtil.addRunnerToBase(game, basesToAdvance, game.getCurrentBatter());

                game.setStrike(0);
                game.setBall(0);
                break;
            case "홈런":
                int runsFromHomeRun = game.getBaseRunners().size() + 1;
                // 점수 처리 로직은 GameStateService에서 처리
                GameLogicUtil.resetBases(game);
                game.setStrike(0);
                game.setBall(0);
                break;
            case "뜬공 아웃":
                game.setOut(game.getOut() + 1);
                game.setStrike(0);
                game.setBall(0);
                break;
            case "삼진 아웃":
                game.setOut(game.getOut() + 1);
                game.setStrike(0);
                game.setBall(0);
                break;
            case "땅볼 아웃":
            case "병살타":
                String groundBallResult = GameLogicUtil.processGroundBall(game, game.getCurrentBatter());
                game.setStrike(0);
                game.setBall(0);
                break;
            default:
                log.warn("게임 {}: 예상치 못한 타격 결과: {}", gameId, hitResult);
                game.setOut(game.getOut() + 1);
                game.setStrike(0);
                game.setBall(0);
                break;
        }

        return hitResult;
    }

    @Override
    @Transactional
    public String pitcherThrow(String gameId, String pitchType) {
        GameDto game = lifecycleService.getGame(gameId);

        if (game.isGameOver()) {
            throw new InvalidGameStateException("게임이 이미 종료되었습니다.");
        }
        if (game.getCurrentBatter() == null || game.getCurrentPitcher() == null) {
            throw new InvalidGameStateException("현재 타자 또는 투수가 설정되지 않았습니다. 라인업을 먼저 설정해주세요.");
        }
        if (game.getOut() >= 3 && game.getStrike() == 0 && game.getBall() == 0) {
            throw new InvalidGameStateException("현재 공격 이닝이 종료되었습니다. 다음 이닝으로 진행해주세요.");
        }

        String pitchResult = GameLogicUtil.determinePitchResultByStats(game.getCurrentPitcher(),
                game.getCurrentBatter());

        log.info("게임 {}: 투수 {} 투구. 결과: {}",
                gameId, game.getCurrentPitcher().getName(), pitchResult);

        switch (pitchResult) {
            case "스트라이크":
                game.setStrike(game.getStrike() + 1);
                break;
            case "볼":
                game.setBall(game.getBall() + 1);
                break;
        }

        return pitchResult;
    }

    @Override
    public String playComputerTurn(String gameId) {
        GameDto game = lifecycleService.getGame(gameId);

        boolean isComputerOffense = (game.isUserOffense() && !game.isTop()) || (!game.isUserOffense() && game.isTop());

        if (!isComputerOffense) {
            return "유저의 턴입니다.";
        }

        Batter computerBatter = game.getCurrentBatter();
        Pitcher currentPitcher = game.getCurrentPitcher();

        double batterStrikeoutRate = computerBatter.getStrikeOuts()
                / (double) (computerBatter.getPlateAppearances() > 0 ? computerBatter.getPlateAppearances() : 1);
        double pitcherStrikeoutRate = currentPitcher.getStrikeouts()
                / (double) (currentPitcher.getPitchersBattersFaced() > 0 ? currentPitcher.getPitchersBattersFaced()
                        : 1);

        double swingProbability = 0.5 + (batterStrikeoutRate - pitcherStrikeoutRate);
        swingProbability = Math.max(0.2, Math.min(0.9, swingProbability));

        boolean decisionToSwing = (Math.random() < swingProbability);

        int contactStat = GameLogicUtil.calculateContactFromBattingAverage(computerBatter.getBattingAverage());
        double timing;

        if (decisionToSwing) {
            double timingRange = 0.5 - (contactStat / 200.0);
            timing = 0.5 + (Math.random() - 0.5) * timingRange;
            timing = Math.max(0.1, Math.min(0.9, timing));
        } else {
            timing = 0.5;
        }

        return batterSwing(gameId, decisionToSwing, timing);
    }
}