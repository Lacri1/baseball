package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.constant.GameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameValidationServiceImpl implements GameValidationService {

    @Autowired
    private GameLifecycleService lifecycleService;

    @Override
    public boolean isGamePlayable(String gameId) {
        try {
            GameDto game = lifecycleService.getGame(gameId);
            return !game.isGameOver() && game.getCurrentBatter() != null && game.getCurrentPitcher() != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isValidGameState(GameDto game) {
        if (game == null)
            return false;

        // 기본 게임 상태 검증
        if (game.getInning() < 1 || game.getInning() > game.getMaxInning())
            return false;
        if (game.getOut() < 0 || game.getOut() > GameConstants.MAX_OUTS)
            return false;
        if (game.getStrike() < 0 || game.getStrike() > GameConstants.MAX_STRIKES)
            return false;
        if (game.getBall() < 0 || game.getBall() > GameConstants.MAX_BALLS)
            return false;

        return true;
    }

    @Override
    public boolean canBatterSwing(String gameId) {
        try {
            GameDto game = lifecycleService.getGame(gameId);
            return isGamePlayable(gameId) &&
                    game.getOut() < GameConstants.MAX_OUTS &&
                    game.getStrike() < GameConstants.MAX_STRIKES &&
                    game.getBall() < GameConstants.MAX_BALLS;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canPitcherThrow(String gameId) {
        return canBatterSwing(gameId); // 투구와 타격은 같은 조건
    }

    @Override
    public boolean canChangeInning(String gameId) {
        try {
            GameDto game = lifecycleService.getGame(gameId);
            return game.getOut() >= GameConstants.MAX_OUTS;
        } catch (Exception e) {
            return false;
        }
    }
}