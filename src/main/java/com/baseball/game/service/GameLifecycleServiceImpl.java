package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.exception.GameNotFoundException;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.util.GameLogicUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class GameLifecycleServiceImpl implements GameLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(GameLifecycleServiceImpl.class);

    // 게임 데이터를 메모리에 저장하는 HashMap
    private final Map<String, GameDto> games = new HashMap<>();

    @Override
    @Transactional
    public GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense) {
        // 팀 검증
        if (homeTeam == null || awayTeam == null || homeTeam.trim().isEmpty() || awayTeam.trim().isEmpty()) {
            throw new ValidationException("홈팀과 원정팀 이름은 필수입니다.");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new ValidationException("홈팀과 원정팀은 동일할 수 없습니다.");
        }
        if (maxInning <= 0) {
            throw new ValidationException("최대 이닝 수는 1 이상이어야 합니다.");
        }

        GameDto newGame = new GameDto();
        newGame.setGameId(UUID.randomUUID().toString());
        newGame.setHomeTeam(homeTeam);
        newGame.setAwayTeam(awayTeam);
        newGame.setMaxInning(maxInning);
        newGame.setUserOffense(isUserOffense);
        newGame.setInning(1);
        newGame.setTop(true);
        newGame.setOut(0);
        newGame.setStrike(0);
        newGame.setBall(0);
        newGame.setHomeScore(0);
        newGame.setAwayScore(0);
        GameLogicUtil.resetBases(newGame);
        newGame.setGameOver(false);
        newGame.setWinner(null);

        // 초기 타자 및 투수 설정
        newGame.setCurrentBatter(null);
        newGame.setCurrentPitcher(null);
        newGame.setBattingOrder(new ArrayList<>());
        newGame.setPitcherList(new ArrayList<>());
        newGame.setStartingPitcher(null);
        newGame.setHomeStartingPitcher(null);
        newGame.setAwayStartingPitcher(null);
        newGame.setHomeBattingOrder(new ArrayList<>());
        newGame.setAwayBattingOrder(new ArrayList<>());
        newGame.setCurrentBatterIndex(0);

        games.put(newGame.getGameId(), newGame);
        log.info("Created game with ID: {}", newGame.getGameId());

        return newGame;
    }

    @Override
    public GameDto getGame(String gameId) {
        GameDto game = games.get(gameId);
        if (game == null) {
            throw new GameNotFoundException("게임을 찾을 수 없습니다: " + gameId);
        }
        return game;
    }

    @Override
    @Transactional
    public void resetGame(String gameId) {
        GameDto game = getGame(gameId);

        // 게임 상태 초기화
        game.setInning(1);
        game.setTop(true);
        game.setOut(0);
        game.setStrike(0);
        game.setBall(0);
        game.setHomeScore(0);
        game.setAwayScore(0);
        GameLogicUtil.resetBases(game);
        game.setGameOver(false);
        game.setWinner(null);
        game.setCurrentBatterIndex(0);

        log.info("Game reset: {}", gameId);
    }

    @Override
    public boolean existsGame(String gameId) {
        return games.containsKey(gameId);
    }

    /**
     * 내부적으로 게임을 저장하는 메서드 (다른 서비스에서 사용)
     */
    protected void saveGame(GameDto game) {
        games.put(game.getGameId(), game);
    }
}