package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Batter;
import com.baseball.game.exception.InvalidGameStateException;
import com.baseball.game.util.GameLogicUtil;
import com.baseball.game.constant.GameConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional
public class GameStateServiceImpl implements GameStateService {

    private static final Logger log = LoggerFactory.getLogger(GameStateServiceImpl.class);

    @Autowired
    private GameLifecycleService lifecycleService;

    @Autowired
    private GameValidationService validationService;

    @Override
    @Transactional
    public GameDto nextInning(String gameId) {
        GameDto game = lifecycleService.getGame(gameId);

        if (!validationService.canChangeInning(gameId)) {
            throw new InvalidGameStateException("이닝을 전환할 수 없는 상태입니다.");
        }

        if (game.isTop()) {
            game.setTop(false);
        } else {
            game.setInning(game.getInning() + 1);
            game.setTop(true);
        }

        game.setOut(0);
        game.setStrike(0);
        game.setBall(0);
        GameLogicUtil.resetBases(game);

        log.info("Inning changed to: {} {}", game.getInning(), game.isTop() ? "초" : "말");

        return game;
    }

    @Override
    @Transactional
    public GameDto endGame(String gameId) {
        GameDto game = lifecycleService.getGame(gameId);

        game.setGameOver(true);

        if (game.getHomeScore() > game.getAwayScore()) {
            game.setWinner(game.getHomeTeam());
        } else if (game.getAwayScore() > game.getHomeScore()) {
            game.setWinner(game.getAwayTeam());
        } else {
            game.setWinner("무승부");
        }

        log.info("Game ended. Winner: {}", game.getWinner());

        return game;
    }

    @Override
    @Transactional
    public void advanceRunners(String gameId, Integer basesToAdvance) {
        GameDto game = lifecycleService.getGame(gameId);
        GameLogicUtil.advanceRunners(game, basesToAdvance);
    }

    @Override
    @Transactional
    public void checkCount(String gameId) {
        GameDto game = lifecycleService.getGame(gameId);

        if (game.getStrike() >= GameConstants.MAX_STRIKES) {
            game.setOut(game.getOut() + 1);
            game.setStrike(0);
            game.setBall(0);
            advanceBattingOrder(gameId);
            log.debug("Strikeout. Outs: {}", game.getOut());
        }

        if (game.getBall() >= GameConstants.MAX_BALLS) {
            game.setBall(0);
            game.setStrike(0);
            advanceRunners(gameId, 1);
            advanceBattingOrder(gameId);
            log.debug("Walk. Ball count reset.");
        }
    }

    @Override
    @Transactional
    public void advanceBattingOrder(String gameId) {
        GameDto game = lifecycleService.getGame(gameId);

        List<Batter> currentLineup = game.getCurrentOffensiveLineup();
        if (currentLineup.isEmpty()) {
            log.warn("No batting order available");
            return;
        }

        game.setCurrentBatterIndex((game.getCurrentBatterIndex() + 1) % currentLineup.size());
        game.setCurrentBatter(currentLineup.get(game.getCurrentBatterIndex()));

        log.debug("Batting order advanced to: {}", game.getCurrentBatterIndex());
    }

    @Override
    @Transactional
    public void checkGameOver(String gameId) {
        GameDto game = lifecycleService.getGame(gameId);

        if (game.getInning() > GameConstants.MAX_INNINGS && !game.isTop()) {
            if (game.getHomeScore() != game.getAwayScore()) {
                endGame(gameId);
                return;
            }
        }

        if (game.getInning() >= GameConstants.MAX_INNINGS && !game.isTop()) {
            if (game.getHomeScore() == game.getAwayScore()) {
                log.info("Extra innings will continue");
            }
        }
    }

    @Override
    @Transactional
    public void handleScore(String gameId, int score) {
        GameDto game = lifecycleService.getGame(gameId);

        if (game.isTop()) {
            game.setAwayScore(game.getAwayScore() + score);
        } else {
            game.setHomeScore(game.getHomeScore() + score);
        }

        log.info("Score updated. Home: {}, Away: {}", game.getHomeScore(), game.getAwayScore());
    }
}