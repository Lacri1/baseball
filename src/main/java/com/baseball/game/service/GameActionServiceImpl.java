package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.exception.InvalidGameStateException;
import com.baseball.game.exception.ValidationException;
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

    @Autowired
    private GameStateService stateService;

    @Override
    @Transactional
    public String batterSwing(String gameId, Boolean swing, Double timing) {
        // 역할: 한 번의 스윙 액션을 처리하여 카운트/루상/점수 등의 게임 상태를 갱신
        // 전제 조건: 유효한 게임, 현재 타자/투수가 세팅되어 있어야 함
        GameDto game = lifecycleService.getGame(gameId);

        if (game.isGameOver()) {
            throw new InvalidGameStateException("게임이 이미 종료되었습니다.");
        }
        // 유저 공격 턴 확인: 유저가 공격 팀이고 현재 초(away)거나, 유저가 수비 팀이면 현재 말(home)일 때만 스윙 허용
        boolean isUserOffenseNow = (game.isUserOffense() && game.isTop()) || (!game.isUserOffense() && !game.isTop());
        if (!isUserOffenseNow) {
            throw new InvalidGameStateException("유저의 공격 턴이 아닙니다.");
        }
        if (game.getCurrentBatter() == null || game.getCurrentPitcher() == null) {
            throw new InvalidGameStateException("현재 타자 또는 투수가 설정되지 않았습니다. 라인업을 먼저 설정해주세요.");
        }
        if (game.getOut() >= 3 && game.getStrike() == 0 && game.getBall() == 0) {
            throw new InvalidGameStateException("현재 공격 이닝이 종료되었습니다. 다음 이닝으로 진행해주세요.");
        }
        return doSwing(gameId, swing, timing);
    }

    @Override
    @Transactional
    public String pitcherThrow(String gameId, String pitchType) {
        // 역할: 투수의 한 번의 투구 결과(스트라이크/볼)를 결정하고 카운트를 갱신
        // 설계: 타자/투수의 기록 기반 확률 모델을 사용하여 결과 도출
        GameDto game = lifecycleService.getGame(gameId);

        if (game.isGameOver()) {
            throw new InvalidGameStateException("게임이 이미 종료되었습니다.");
        }
        // 유저 수비 턴 확인: 유저가 수비일 때만 투구 가능
        boolean isUserOffenseNow = (game.isUserOffense() && game.isTop()) || (!game.isUserOffense() && !game.isTop());
        boolean isUserDefenseNow = !isUserOffenseNow;
        if (!isUserDefenseNow) {
            throw new InvalidGameStateException("유저의 수비 턴이 아닙니다.");
        }
        if (game.getCurrentBatter() == null || game.getCurrentPitcher() == null) {
            throw new InvalidGameStateException("현재 타자 또는 투수가 설정되지 않았습니다. 라인업을 먼저 설정해주세요.");
        }
        if (game.getOut() >= 3 && game.getStrike() == 0 && game.getBall() == 0) {
            throw new InvalidGameStateException("현재 공격 이닝이 종료되었습니다. 다음 이닝으로 진행해주세요.");
        }

        if (pitchType == null) {
            throw new ValidationException("투구 유형을 지정해주세요.");
        }
        String intended = pitchType.trim().toLowerCase();
        if (!"strike".equals(intended) && !"ball".equals(intended)) {
            throw new ValidationException("투구 유형은 'strike' 또는 'ball' 이어야 합니다.");
        }

        String pitchResult = GameLogicUtil.determinePitchResult(game.getCurrentPitcher(), intended);
        // 저장: 직전 투구 존("strike"/"ball")
        game.setLastPitchType("스트라이크".equals(pitchResult) ? "strike" : "ball");

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
        // 카운트 검사: 삼진/볼넷 처리 및 타순 진행
        stateService.checkCount(gameId);
        // 삼진으로 아웃 누적이 3 이상이면 이닝 전환
        if (game.getOut() >= 3 && game.getStrike() == 0 && game.getBall() == 0) {
            stateService.nextInning(gameId);
        }

        // 현재 턴이 컴퓨터 공격이면, 즉시 컴퓨터 타격 자동 진행
        boolean isComputerOffense = (game.isUserOffense() && !game.isTop()) || (!game.isUserOffense() && game.isTop());
        if (isComputerOffense) {
            try {
                String computerResult = playComputerTurn(gameId);
                return pitchResult + " | 컴퓨터 타격: " + computerResult;
            } catch (Exception e) {
                log.warn("컴퓨터 타격 자동 진행 중 오류 발생", e);
            }
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

        // 노스윙이면 이미 pitcherThrow에서 카운트를 처리했으므로 추가 처리하지 않음
        if (!decisionToSwing) {
            return "스윙 안함";
        }

        int contactStat = GameLogicUtil.calculateContactFromBattingAverage(computerBatter.getBattingAverage());
        double timing;

        double timingRange = 0.5 - (contactStat / 200.0);
        timing = 0.5 + (Math.random() - 0.5) * timingRange;
        timing = Math.max(0.1, Math.min(0.9, timing));

        return doSwing(gameId, true, timing);
    }

    private String doSwing(String gameId, Boolean swing, Double timing) {
        GameDto game = lifecycleService.getGame(gameId);
        String pitchZone = game.getLastPitchType() != null ? game.getLastPitchType() : "strike";

        String hitResult = GameLogicUtil.determineHitResultWithTiming(
                swing,
                game.getCurrentPitcher(),
                pitchZone,
                timing,
                game.getCurrentBatter());

        log.info("게임 {}: 타자 {} (타이밍: {}) 스윙: {}, 투수 {} 투구 결과: {}, 타격 결과: {}",
                gameId, game.getCurrentBatter().getName(), timing, swing, game.getCurrentPitcher().getName(),
                pitchZone, hitResult);

        switch (hitResult) {
            case "스트라이크":
                game.setStrike(game.getStrike() + 1);
                stateService.checkCount(gameId);
                break;
            case "볼":
                game.setBall(game.getBall() + 1);
                stateService.checkCount(gameId);
                break;
            case "헛스윙":
                game.setStrike(game.getStrike() + 1);
                stateService.checkCount(gameId);
                break;
            case "안타":
            case "2루타":
            case "3루타": {
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
                stateService.advanceBattingOrder(gameId);
                break;
            }
            case "홈런":
                GameLogicUtil.resetBases(game);
                game.setStrike(0);
                game.setBall(0);
                stateService.advanceBattingOrder(gameId);
                break;
            case "뜬공 아웃":
            case "삼진 아웃":
            case "땅볼 아웃":
            case "병살타": {
                int beforeOuts = game.getOut();
                if ("땅볼 아웃".equals(hitResult) || "병살타".equals(hitResult)) {
                    String ignored = GameLogicUtil.processGroundBall(game, game.getCurrentBatter());
                }
                if ("삼진 아웃".equals(hitResult) || "뜬공 아웃".equals(hitResult)) {
                    game.setOut(beforeOuts + 1);
                }
                game.setStrike(0);
                game.setBall(0);
                if (game.getOut() >= 3)
                    stateService.nextInning(gameId);
                else
                    stateService.advanceBattingOrder(gameId);
                break;
            }
            default: {
                int beforeOuts = game.getOut();
                game.setOut(beforeOuts + 1);
                game.setStrike(0);
                game.setBall(0);
                if (game.getOut() >= 3)
                    stateService.nextInning(gameId);
                else
                    stateService.advanceBattingOrder(gameId);
                break;
            }
        }
        return hitResult;
    }
}