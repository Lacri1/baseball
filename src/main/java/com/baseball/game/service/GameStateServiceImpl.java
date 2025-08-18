package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Batter;
import com.baseball.game.mapper.MemberMapper;
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

    @Autowired(required = false)
    private MemberMapper memberMapper; // 로그인/회원 기능이 있는 경우에만 주입

    @Override
    @Transactional
    public GameDto nextInning(String gameId) {
        // 역할: 이닝 전환 시 초/말 토글 및 이닝 증가, 카운트/루상 초기화
        // 전제: 현재 이닝이 종료 가능한 상태인지 별도 검증 서비스에서 확인
        GameDto game = lifecycleService.getGame(gameId);

        if (!validationService.canChangeInning(gameId)) {
            throw new InvalidGameStateException("이닝을 전환할 수 없는 상태입니다.");
        }

        // 이전 공격팀의 타순을 다음 타자로 넘겨준다 (3아웃으로 이닝 종료된 타자는 다음 타석에서 제외)
        boolean wasTop = game.isTop();
        if (wasTop) {
            // 원정팀이 공격을 마침 → awayBatterIndex를 다음 타자로 이동
            List<Batter> away = game.getAwayBattingOrder();
            if (away != null && !away.isEmpty()) {
                int next = (game.getAwayBatterIndex() + 1) % away.size();
                game.setAwayBatterIndex(next);
            }
        } else {
            // 홈팀이 공격을 마침 → homeBatterIndex를 다음 타자로 이동
            List<Batter> home = game.getHomeBattingOrder();
            if (home != null && !home.isEmpty()) {
                int next = (game.getHomeBatterIndex() + 1) % home.size();
                game.setHomeBatterIndex(next);
            }
        }

        if (wasTop) {
            // 초에서 말로: 다음 타석은 홈팀의 이어지는 타자
            game.setTop(false);
        } else {
            // 말에서 다음 이닝 초로: 최종 이닝을 넘기지 않는다
            if (game.getInning() >= game.getMaxInning()) {
                // 규정 이닝 종료 → 즉시 게임 종료
                endGame(gameId);
                return game;
            }
            game.setInning(game.getInning() + 1);
            game.setTop(true);
        }

        game.setOut(0);
        game.setStrike(0);
        game.setBall(0);
        GameLogicUtil.resetBases(game);

        // 초/말 전환에 따라 현재 투수/타자를 재설정 (야구 규칙: 타순은 팀별로 이어짐)
        java.util.List<com.baseball.game.dto.Batter> newOffense = game.getCurrentOffensiveLineup();
        if (newOffense != null && !newOffense.isEmpty()) {
            if (game.isTop()) {
                // 원정팀 공격: awayBatterIndex 사용
                int idx = game.getAwayBatterIndex() % newOffense.size();
                game.setCurrentBatterIndex(idx);
                game.setCurrentBatter(newOffense.get(idx));
            } else {
                // 홈팀 공격: homeBatterIndex 사용
                int idx = game.getHomeBatterIndex() % newOffense.size();
                game.setCurrentBatterIndex(idx);
                game.setCurrentBatter(newOffense.get(idx));
            }
        } else {
            game.setCurrentBatter(null);
            game.setCurrentBatterIndex(0);
        }
        // 투수는 수비 팀 선발 투수 유지 (현재 설계상 선발 고정)
        game.setCurrentPitcher(game.getCurrentDefensivePitcher());

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

        // 회원 승/패/무/게임 수 업데이트 (userId를 GameDto에 세팅한 뒤 사용)
        try {
            if (memberMapper != null && game.getUserId() != null && !game.getUserId().trim().isEmpty()) {
                String userId = game.getUserId();
                memberMapper.incrementGame(userId);

                String winner = game.getWinner();
                if ("무승부".equals(winner)) {
                    memberMapper.incrementDraw(userId);
                } else {
                    boolean userIsAway = game.isUserOffense();
                    String userTeam = userIsAway ? game.getAwayTeam() : game.getHomeTeam();
                    if (userTeam != null && userTeam.equals(winner)) {
                        memberMapper.incrementWin(userId);
                    } else {
                        memberMapper.incrementLose(userId);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to update member game result", e);
        }

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
        // 역할: 스트라이크/볼 누적에 따른 삼진/볼넷 처리와 타순 진행
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
            // 볼넷: 주자 강제 1루 진루 + 타자 1루 진루
            advanceRunners(gameId, 1);
            GameLogicUtil.addRunnerToBase(game, 1, game.getCurrentBatter());
            advanceBattingOrder(gameId);
            log.debug("Walk. Ball count reset.");
        }
    }

    @Override
    @Transactional
    public void advanceBattingOrder(String gameId) {
        // 역할: 현재 공격 팀의 타순을 한 명 앞으로 진행하고 현재 타자를 갱신
        GameDto game = lifecycleService.getGame(gameId);

        List<Batter> currentLineup = game.getCurrentOffensiveLineup();
        if (currentLineup.isEmpty()) {
            log.warn("No batting order available");
            return;
        }

        // 팀별로 타순 인덱스를 관리 (야구 규칙)
        if (game.isTop()) {
            int next = (game.getAwayBatterIndex() + 1) % currentLineup.size();
            game.setAwayBatterIndex(next);
            game.setCurrentBatterIndex(next);
            game.setCurrentBatter(currentLineup.get(next));
        } else {
            int next = (game.getHomeBatterIndex() + 1) % currentLineup.size();
            game.setHomeBatterIndex(next);
            game.setCurrentBatterIndex(next);
            game.setCurrentBatter(currentLineup.get(next));
        }

        log.debug("Batting order advanced to: {}", game.getCurrentBatterIndex());
    }

    @Override
    @Transactional
    public void checkGameOver(String gameId) {
        // 역할: 규정 이닝 도달/초말 상태에 따른 경기 종료 조건 확인
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
        // 역할: 현재 공격 팀에 점수를 가산
        GameDto game = lifecycleService.getGame(gameId);

        if (game.isTop()) {
            game.setAwayScore(game.getAwayScore() + score);
        } else {
            game.setHomeScore(game.getHomeScore() + score);
        }

        log.info("Score updated. Home: {}, Away: {}", game.getHomeScore(), game.getAwayScore());
    }
}