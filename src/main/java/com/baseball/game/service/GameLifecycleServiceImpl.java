package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.exception.GameNotFoundException;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.util.GameLogicUtil;
import com.baseball.game.mapper.BatterMapper;
import com.baseball.game.mapper.PitcherMapper;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class GameLifecycleServiceImpl implements GameLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(GameLifecycleServiceImpl.class);

    // 게임 데이터를 메모리에 저장하는 HashMap
    // 설계 의도: 간단한 러닝 환경에서는 인메모리로 충분하며, 운영에서는 Redis 저장소로 대체 가능
    private final Map<String, GameDto> games = new HashMap<>();

    @org.springframework.beans.factory.annotation.Autowired
    private BatterMapper batterMapper;

    @org.springframework.beans.factory.annotation.Autowired
    private PitcherMapper pitcherMapper;

    @Override
    @Transactional
    public GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense) {
        // 팀/입력값 검증
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
        // userId는 컨트롤러 요청에서 내려온 정보를 서비스 계층에서 세팅하도록 확장 (별도 오버로드 고려)
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

        // 인메모리 기본 라인업 사용
        applyInMemoryDefaultLineups(newGame, homeTeam, awayTeam);

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
        // 라인업/선발투수도 기본값으로 재설정 (게임 생성 당시 팀 기준)
        game.setCurrentBatterIndex(0);
        game.setHomeBatterIndex(0);
        game.setAwayBatterIndex(0);
        applyInMemoryDefaultLineups(game, game.getHomeTeam(), game.getAwayTeam());

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

    @Override
    public void saveGame(String gameId) {
        GameDto game = games.get(gameId);
        if (game == null)
            return;
        try {
            // Redis 저장소가 구성된 경우 이를 통해 저장하도록 확장 가능
            com.baseball.game.repository.GameRepository repo = null;
            try {
                repo = (com.baseball.game.repository.GameRepository) (new org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor());
            } catch (Throwable ignored) {
            }
            // 현재 구현은 인메모리 유지. 외부에서 RedisGameRepository 사용 시 이 메서드를 오버라이드/주입 구조로 전환 권장.
        } catch (Throwable ignored) {
        }
    }

    private List<Batter> orderBattersByNames(List<String> names) {
        if (names == null || names.isEmpty())
            return new ArrayList<>();
        List<Batter> fetched = (batterMapper != null) ? batterMapper.findByNames(names) : null;
        Map<String, Batter> byName = new java.util.HashMap<>();
        if (fetched != null) {
            for (Batter b : fetched) {
                if (b != null && b.getName() != null) {
                    // 타율이 비어 있으면 계산값 반영
                    if (b.getAtBats() > 0 && (b.getBattingAverage() == 0.0)) {
                        b.setBattingAverage(b.calculateBattingAverage());
                    }
                    byName.put(b.getName(), b);
                }
            }
        }
        List<Batter> ordered = new ArrayList<>();
        for (String n : names) {
            Batter b = byName.get(n);
            if (b != null) {
                ordered.add(b);
            }
        }
        return ordered;
    }

    private void applyInMemoryDefaultLineups(GameDto game, String homeTeam, String awayTeam) {
        try {
            List<String> homeNames = ComputerLineupProvider.getDefaultBattingOrder(homeTeam);
            List<String> awayNames = ComputerLineupProvider.getDefaultBattingOrder(awayTeam);

            // 매퍼가 없으면 이름만으로 간단한 객체 생성
            List<Batter> homeBatters;
            List<Batter> awayBatters;
            if (batterMapper != null) {
                homeBatters = orderBattersByNames(homeNames);
                awayBatters = orderBattersByNames(awayNames);
            } else {
                homeBatters = new ArrayList<>();
                for (String n : homeNames)
                    homeBatters.add(new com.baseball.game.dto.Batter(n, homeTeam));
                awayBatters = new ArrayList<>();
                for (String n : awayNames)
                    awayBatters.add(new com.baseball.game.dto.Batter(n, awayTeam));
            }

            String homeSPName = ComputerLineupProvider.getDefaultStartingPitcher(homeTeam);
            String awaySPName = ComputerLineupProvider.getDefaultStartingPitcher(awayTeam);

            Pitcher homeSP = null;
            Pitcher awaySP = null;
            if (pitcherMapper != null) {
                homeSP = homeSPName != null ? pitcherMapper.findByName(homeSPName) : null;
                awaySP = awaySPName != null ? pitcherMapper.findByName(awaySPName) : null;
            } else {
                if (homeSPName != null)
                    homeSP = new com.baseball.game.dto.Pitcher(homeSPName, homeTeam);
                if (awaySPName != null)
                    awaySP = new com.baseball.game.dto.Pitcher(awaySPName, awayTeam);
            }

            game.setHomeBattingOrder(homeBatters);
            game.setAwayBattingOrder(awayBatters);
            game.setHomeStartingPitcher(homeSP);
            game.setAwayStartingPitcher(awaySP);

            game.setCurrentBatterIndex(0);
            if (game.isTop()) {
                if (!game.getAwayBattingOrder().isEmpty()) {
                    game.setCurrentBatter(game.getAwayBattingOrder().get(0));
                }
                game.setCurrentPitcher(game.getHomeStartingPitcher());
            } else {
                if (!game.getHomeBattingOrder().isEmpty()) {
                    game.setCurrentBatter(game.getHomeBattingOrder().get(0));
                }
                game.setCurrentPitcher(game.getAwayStartingPitcher());
            }
        } catch (Throwable t) {
            log.warn("인메모리 기본 라인업 적용 실패. 빈 라인업으로 생성됩니다.", t);
            game.setCurrentBatter(null);
            game.setCurrentPitcher(null);
            game.setHomeStartingPitcher(null);
            game.setAwayStartingPitcher(null);
            game.setHomeBattingOrder(new ArrayList<>());
            game.setAwayBattingOrder(new ArrayList<>());
            game.setCurrentBatterIndex(0);
        }
    }
}