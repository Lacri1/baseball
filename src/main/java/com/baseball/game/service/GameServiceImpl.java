package com.baseball.game.service;

import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.util.GameLogicUtil;
import com.baseball.game.exception.GameNotFoundException;
import com.baseball.game.exception.InvalidGameStateException;
import com.baseball.game.exception.ValidationException;
// import com.baseball.game.repository.GameRepository; // DB 연동 관련이므로 주석 처리 또는 제거
import com.baseball.game.mapper.BatterMapper;
import com.baseball.game.mapper.PitcherMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
import java.util.stream.Collectors;

import lombok.Setter;

@Service
@Transactional // 트랜잭션 어노테이션은 DB 연동 시 유효하지만, 메모리 기반에서는 큰 의미 없음 (그래도 제거하지는 않음)
public class GameServiceImpl implements GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);

    // @Setter(onMethod_ = @Autowired)
    // private GameRepository gameRepository; // DB 연동을 사용하지 않으므로 제거

    @Setter(onMethod_ = @Autowired)
    private BatterMapper batterMapper;

    @Setter(onMethod_ = @Autowired)
    private PitcherMapper pitcherMapper;

    // 게임 데이터를 메모리에 저장하는 HashMap
    private Map<String, GameDto> games = new HashMap<>();

    @Override
    @Transactional
    public GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense) {
        // 팀 검증 (실제 팀 데이터를 조회하는 로직 필요)
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
        newGame.setGameId(UUID.randomUUID().toString()); // 고유한 게임 ID 생성
        newGame.setHomeTeam(homeTeam);
        newGame.setAwayTeam(awayTeam);
        newGame.setMaxInning(maxInning);
        newGame.setIsUserOffense(isUserOffense);
        newGame.setInning(1);
        newGame.setTop(true);
        newGame.setOut(0);
        newGame.setStrike(0);
        newGame.setBall(0);
        newGame.setHomeScore(0);
        newGame.setAwayScore(0);
        GameLogicUtil.resetBases(newGame); // 베이스 초기화
        newGame.setGameOver(false);
        newGame.setWinner(null);

        // 초기 타자 및 투수 설정은 라인업 설정 API 호출 후 이루어집니다.
        newGame.setCurrentBatter(null);
        newGame.setCurrentPitcher(null);
        newGame.setBattingOrder(new ArrayList<>());
        newGame.setPitcherList(new ArrayList<>()); // 투수 리스트 (계투 등 고려)
        newGame.setStartingPitcher(null); // 현재 등판 투수
        newGame.setHomeStartingPitcher(null); // 홈팀 선발
        newGame.setAwayStartingPitcher(null); // 원정팀 선발
        newGame.setHomeBattingOrder(new ArrayList<>());
        newGame.setAwayBattingOrder(new ArrayList<>());
        newGame.setCurrentBatterIndex(0);


        games.put(newGame.getGameId(), newGame); // 게임 생성 시 Map에 저장
        logger.info("Created game with ID: {}", newGame.getGameId());

        // gameRepository.save(newGame); // DB 저장 로직 제거됨
        return newGame;
    }

    @Override
    public GameDto getGame(String gameId) {
        GameDto game = games.get(gameId); // Map에서 게임 조회
        if (game == null) {
            throw new GameNotFoundException("게임 ID: " + gameId + "를 찾을 수 없습니다.");
        }
        return game;
    }

    @Override
    @Transactional
    public String batterSwing(String gameId, Boolean swing, Double timing) {
        GameDto game = getGame(gameId); // Map에서 게임 조회

        if (game.isGameOver()) {
            throw new InvalidGameStateException("게임이 이미 종료되었습니다.");
        }
        if (game.getCurrentBatter() == null || game.getCurrentPitcher() == null) {
            throw new InvalidGameStateException("현재 타자 또는 투수가 설정되지 않았습니다. 라인업을 먼저 설정해주세요.");
        }
        if (game.getOut() >= 3 && game.getStrike() == 0 && game.getBall() == 0) {
            throw new InvalidGameStateException("현재 공격 이닝이 종료되었습니다. 다음 이닝으로 진행해주세요.");
        }

        // 임시로 투구 유형을 "strike"로 가정합니다. 실제로는 투수 투구 액션에서 받아야 합니다.
        String actualPitchType = "strike"; // 또는 클라이언트로부터 전달받는 pitchType

        // GameLogicUtil.determineHitResultWithTiming을 사용하여 타격 결과 결정
        String hitResult = GameLogicUtil.determineHitResultWithTiming(
            swing,
            game.getCurrentPitcher(),
            actualPitchType, // 투수가 던진 공의 유형 (스트라이크 존/볼 존)
            timing,
            game.getCurrentBatter()
        );

        logger.info("게임 {}: 타자 {} (타이밍: {}) 스윙: {}, 투수 {} 투구 결과: {}, 타격 결과: {}",
            gameId, game.getCurrentBatter().getName(), timing, swing, game.getCurrentPitcher().getName(), actualPitchType, hitResult);

        // 결과에 따른 게임 상태 업데이트
        switch (hitResult) {
            case "스트라이크": // 노스윙 스트라이크
                game.setStrike(game.getStrike() + 1);
                break;
            case "볼": // 노스윙 볼
                game.setBall(game.getBall() + 1);
                break;
            case "헛스윙": // 스윙했지만 헛스윙 (파울 포함)
                game.setStrike(game.getStrike() + 1);
                // 2스트라이크 이후 헛스윙 파울은 스트라이크로 계산하지 않음 -> GameLogicUtil에서 이 부분을 처리하면 좋음.
                break;
            case "안타":
            case "2루타":
            case "3루타":
                int basesToAdvance = 0;
                if (hitResult.equals("안타")) basesToAdvance = 1;
                else if (hitResult.equals("2루타")) basesToAdvance = 2;
                else if (hitResult.equals("3루타")) basesToAdvance = 3;

                // 기존 주자들 진루 (득점 처리 포함)
                GameLogicUtil.advanceRunners(game, basesToAdvance);
                // 타자 본인도 해당 베이스에 진루
                GameLogicUtil.addRunnerToBase(game, basesToAdvance, game.getCurrentBatter());

                game.setStrike(0);
                game.setBall(0);
                advanceBattingOrder(game); // 다음 타자로 변경
                break;
            case "홈런": // GameLogicUtil에서 "홈런!" -> "홈런"으로 통일됨
                int runsFromHomeRun = game.getBaseRunners().size() + 1; // 베이스 주자 수 + 타자 본인
                handleScore(game, runsFromHomeRun); // 점수 처리

                GameLogicUtil.resetBases(game); // 모든 베이스 초기화
                game.setStrike(0);
                game.setBall(0);
                advanceBattingOrder(game);
                break;
            case "뜬공 아웃":
                game.setOut(game.getOut() + 1);
                game.setStrike(0);
                game.setBall(0);
                advanceBattingOrder(game);
                break;
            case "삼진 아웃": // 헛스윙 삼진 포함
                game.setOut(game.getOut() + 1);
                game.setStrike(0);
                game.setBall(0);
                advanceBattingOrder(game);
                break;
            case "땅볼 아웃":
            case "병살타": // "병살타!" -> "병살타"로 GameLogicUtil에서 통일 필요
                // GameLogicUtil.processGroundBall에서 이미 아웃 카운트와 베이스 처리
                String groundBallResult = GameLogicUtil.processGroundBall(game, game.getCurrentBatter());
                game.setStrike(0);
                game.setBall(0);
                // 병살타 시 타점 처리 필요 (예: 3루 주자 홈인 시)
                advanceBattingOrder(game);
                // 클라이언트에게 반환할 결과에 groundBallResult 포함 고려 (현재는 hitResult만 반환)
                break;
            default: // 예상치 못한 결과
                logger.warn("게임 {}: 예상치 못한 타격 결과: {}", gameId, hitResult);
                game.setOut(game.getOut() + 1); // 안전을 위해 아웃 처리
                game.setStrike(0);
                game.setBall(0);
                advanceBattingOrder(game);
                break;
        }

        checkCount(game); // 스트라이크, 볼, 아웃 카운트 확인 및 처리
        checkGameOver(game); // 게임 종료 여부 확인

        // gameRepository.save(game); // DB 저장 로직 제거됨
        return hitResult;
    }

    @Override
    @Transactional
    public String pitcherThrow(String gameId, String pitchType) {
        GameDto game = getGame(gameId); // Map에서 게임 조회

        if (game.isGameOver()) {
            throw new InvalidGameStateException("게임이 이미 종료되었습니다.");
        }
        if (game.getCurrentBatter() == null || game.getCurrentPitcher() == null) {
            throw new InvalidGameStateException("현재 타자 또는 투수가 설정되지 않았습니다. 라인업을 먼저 설정해주세요.");
        }
        if (game.getOut() >= 3 && game.getStrike() == 0 && game.getBall() == 0) {
            throw new InvalidGameStateException("현재 공격 이닝이 종료되었습니다. 다음 이닝으로 진행해주세요.");
        }

        // 스윙 없이 투구 결과만 계산 (볼/스트라이크 판정)
        String pitchResult = GameLogicUtil.determinePitchResult(game.getCurrentPitcher(), pitchType);

        logger.info("게임 {}: 투수 {} 투구 ({}). 결과: {}",
            gameId, game.getCurrentPitcher().getName(), pitchType, pitchResult);

        switch (pitchResult) {
            case "스트라이크":
                game.setStrike(game.getStrike() + 1);
                break;
            case "볼":
                game.setBall(game.getBall() + 1);
                break;
        }

        checkCount(game); // 스트라이크, 볼, 아웃 카운트 확인 및 처리
        checkGameOver(game); // 게임 종료 여부 확인

        // gameRepository.save(game); // DB 저장 로직 제거됨
        return pitchResult;
    }

    @Override
    @Transactional
    public GameDto nextInning(String gameId) {
        GameDto game = getGame(gameId); // Map에서 게임 조회

        // 현재 이닝의 말 공격이 끝났다면 다음 이닝으로, 아니면 공수 교대
        if (game.getOut() < 3) { // 3아웃이 안됐는데 다음 이닝 요청시
            throw new InvalidGameStateException("아직 현재 이닝이 끝나지 않았습니다 (3아웃이 아닙니다).");
        }

        if (game.isTop()) { // 현재 이닝 초였으면 -> 말로
            game.setTop(false);
            game.setOut(0);
            game.setStrike(0);
            game.setBall(0);
            GameLogicUtil.resetBases(game);
            // 원정팀 투수 -> 홈팀 타자
            game.setCurrentPitcher(game.getAwayStartingPitcher()); // 또는 현재 등판 중인 투수
            game.setBattingOrder(game.getHomeBattingOrder()); // 홈팀 타순으로 변경
            game.setCurrentBatterIndex(0);
            game.setCurrentBatter(game.getHomeBattingOrder().get(game.getCurrentBatterIndex()));
            logger.info("게임 {}: {}회 말로 진행. 현재 타자: {}", gameId, game.getInning(), game.getCurrentBatter().getName());
        } else { // 현재 이닝 말이었으면 -> 다음 이닝 초로
            game.setInning(game.getInning() + 1);
            game.setTop(true);
            game.setOut(0);
            game.setStrike(0);
            game.setBall(0);
            GameLogicUtil.resetBases(game);
            // 홈팀 투수 -> 원정팀 타자
            game.setCurrentPitcher(game.getHomeStartingPitcher()); // 또는 현재 등판 중인 투수
            game.setBattingOrder(game.getAwayBattingOrder()); // 원정팀 타순으로 변경
            game.setCurrentBatterIndex(0);
            game.setCurrentBatter(game.getAwayBattingOrder().get(game.getCurrentBatterIndex()));
            logger.info("게임 {}: {}회 초로 진행. 현재 타자: {}", gameId, game.getInning(), game.getCurrentBatter().getName());
        }

        checkGameOver(game); // 게임 종료 여부 다시 확인

        // gameRepository.save(game); // DB 저장 로직 제거됨
        return game;
    }

    @Override
    @Transactional
    public GameDto endGame(String gameId) {
        GameDto game = getGame(gameId); // Map에서 게임 조회
        game.setGameOver(true);
        // 승자 결정 로직 (점수 비교 등)
        if (game.getHomeScore() > game.getAwayScore()) {
            game.setWinner(game.getHomeTeam());
        } else if (game.getAwayScore() > game.getHomeScore()) {
            game.setWinner(game.getAwayTeam());
        } else {
            game.setWinner("무승부"); // 동점일 경우 무승부 처리
        }
        logger.info("게임 {} 종료. 승자: {}", gameId, game.getWinner());
        // gameRepository.save(game); // DB 저장 로직 제거됨
        return game;
    }

    @Override
    @Transactional
    public void advanceRunners(String gameId, Integer basesToAdvance) {
        GameDto game = getGame(gameId); // Map에서 게임 조회
        if (game.isGameOver()) {
            throw new InvalidGameStateException("게임이 이미 종료되었습니다.");
        }
        GameLogicUtil.advanceRunners(game, basesToAdvance);
        // gameRepository.save(game); // DB 저장 로직 제거됨
        logger.info("게임 {}: 주자들이 {} 베이스 진루했습니다.", gameId, basesToAdvance);
    }

    @Override
    public String getGameStats(String gameId) {
        GameDto game = getGame(gameId); // Map에서 게임 조회
        StringBuilder stats = new StringBuilder();
        stats.append(String.format("게임 ID: %s\n", game.getGameId()));
        stats.append(String.format("이닝: %d회 %s\n", game.getInning(), game.isTop() ? "초" : "말"));
        stats.append(String.format("현재 점수: %s %d : %d %s\n", game.getAwayTeam(), game.getAwayScore(), game.getHomeScore(), game.getHomeTeam()));
        stats.append(String.format("아웃: %d, 스트라이크: %d, 볼: %d\n", game.getOut(), game.getStrike(), game.getBall()));
        stats.append("루상 주자: ");
        if (game.getBases()[1] != null) stats.append("1루: ").append(game.getBases()[1].getName()).append(" ");
        if (game.getBases()[2] != null) stats.append("2루: ").append(game.getBases()[2].getName()).append(" ");
        if (game.getBases()[3] != null) stats.append("3루: ").append(game.getBases()[3].getName()).append(" ");
        if (game.getBases()[1] == null && game.getBases()[2] == null && game.getBases()[3] == null) stats.append("없음");
        stats.append("\n");

        if (game.getCurrentBatter() != null) {
            stats.append(String.format("현재 타자: %s\n",
                game.getCurrentBatter().getName()));
        }
        if (game.getCurrentPitcher() != null) {
            stats.append(String.format("현재 투수: %s\n",
                game.getCurrentPitcher().getName()));
        }
        if (game.isGameOver()) {
            stats.append(String.format("게임 종료! 승자: %s\n", game.getWinner()));
        }
        return stats.toString();
    }

    @Override
    @Transactional
    public void setTeamLineupAndPitcher(String gameId, String teamName, List<String> battingOrderPlayerNames, String startingPitcherName) {
        GameDto game = getGame(gameId); // Map에서 게임 조회

        // 타자 정보 로드 및 설정 (DB가 아닌 매퍼를 통해 가상의 데이터 또는 별도 데이터 소스에서 로드한다고 가정)
        Map<String, Batter> availableBatters = batterMapper.findByTeam(teamName)
                                                        .stream()
                                                        .collect(Collectors.toMap(Batter::getName, b -> b));

        List<Batter> orderedBatters = new ArrayList<>();
        for (String playerName : battingOrderPlayerNames) {
            Batter batter = availableBatters.get(playerName);
            if (batter == null) {
                throw new ValidationException("라인업에 포함된 타자 '" + playerName + "'를 팀 '" + teamName + "'에서 찾을 수 없습니다.");
            }
            orderedBatters.add(batter);
        }

        if (orderedBatters.size() != battingOrderPlayerNames.size() || orderedBatters.size() != 9) {
            throw new ValidationException("라인업은 9명의 선수로 정확히 구성되어야 합니다. 누락되거나 중복된 선수가 있는지 확인해주세요.");
        }


        // 타순 설정
        if (game.getHomeTeam().equals(teamName)) {
            game.setHomeBattingOrder(orderedBatters);
            logger.info("게임 {}: 홈팀 타순 설정 완료.", gameId);
        } else if (game.getAwayTeam().equals(teamName)) {
            game.setAwayBattingOrder(orderedBatters);
            logger.info("게임 {}: 원정팀 타순 설정 완료.", gameId);
        } else {
            throw new ValidationException("유효하지 않은 팀 이름입니다: " + teamName);
        }

        // 투수 정보 로드 및 설정
        Pitcher pitcher = pitcherMapper.findByName(startingPitcherName);
        if (pitcher == null || !pitcher.getTeam().equals(teamName)) {
            throw new ValidationException("팀 " + teamName + "에서 선발 투수 '" + startingPitcherName + "'를 찾을 수 없거나 해당 팀 소속이 아닙니다.");
        }

        // 해당 팀의 startingPitcher 필드에 설정
        if (game.getHomeTeam().equals(teamName)) {
            game.setHomeStartingPitcher(pitcher);
            logger.info("게임 {}: 홈팀 선발 투수 설정 완료: {}", gameId, pitcher.getName());
        } else if (game.getAwayTeam().equals(teamName)) {
            game.setAwayStartingPitcher(pitcher);
            logger.info("게임 {}: 원정팀 선발 투수 설정 완료: {}", gameId, pitcher.getName());
        }

        // 게임의 현재 타자/투수 초기 설정 (홈팀/원정팀 라인업이 모두 설정되었을 때 한 번만 수행)
        if (game.getHomeBattingOrder() != null && !game.getHomeBattingOrder().isEmpty() &&
            game.getAwayBattingOrder() != null && !game.getAwayBattingOrder().isEmpty() &&
            game.getHomeStartingPitcher() != null && game.getAwayStartingPitcher() != null &&
            game.getCurrentBatter() == null && game.getCurrentPitcher() == null) { // 이미 설정되어 있지 않을 때만
            
            // 사용자가 공격팀인 경우
            if (game.isIsUserOffense()) {
                // 사용자가 원정팀(awayTeam)을 선택했고 초 공격이라면
                if (game.isTop()) { // 1회 초: 원정팀 공격 (사용자 팀), 홈팀 수비 (컴퓨터 팀)
                    game.setCurrentPitcher(game.getHomeStartingPitcher());
                    game.setBattingOrder(game.getAwayBattingOrder()); // 원정팀(사용자) 타순
                    game.setCurrentBatterIndex(0);
                    game.setCurrentBatter(game.getAwayBattingOrder().get(game.getCurrentBatterIndex()));
                } else { // 1회 말: 홈팀 공격 (사용자 팀), 원정팀 수비 (컴퓨터 팀)
                    game.setCurrentPitcher(game.getAwayStartingPitcher());
                    game.setBattingOrder(game.getHomeBattingOrder()); // 홈팀(사용자) 타순
                    game.setCurrentBatterIndex(0);
                    game.setCurrentBatter(game.getHomeBattingOrder().get(game.getCurrentBatterIndex()));
                }
            } else { // 사용자가 수비팀인 경우
                // 사용자가 홈팀(homeTeam)을 선택했고 초 공격이라면 (컴퓨터 팀 공격)
                if (game.isTop()) { // 1회 초: 원정팀 공격 (컴퓨터 팀), 홈팀 수비 (사용자 팀)
                    game.setCurrentPitcher(game.getHomeStartingPitcher());
                    game.setBattingOrder(game.getAwayBattingOrder()); // 원정팀(컴퓨터) 타순
                    game.setCurrentBatterIndex(0);
                    game.setCurrentBatter(game.getAwayBattingOrder().get(game.getCurrentBatterIndex()));
                } else { // 1회 말: 홈팀 공격 (컴퓨터 팀), 원정팀 수비 (사용자 팀)
                    game.setCurrentPitcher(game.getAwayStartingPitcher());
                    game.setBattingOrder(game.getHomeBattingOrder()); // 홈팀(컴퓨터) 타순
                    game.setCurrentBatterIndex(0);
                    game.setCurrentBatter(game.getHomeBattingOrder().get(game.getCurrentBatterIndex()));
                }
            }
            
            logger.info("게임 {}: 초기 타자/투수 설정 완료. 현재 타자: {}, 현재 투수: {}",
                gameId, game.getCurrentBatter() != null ? game.getCurrentBatter().getName() : "없음",
                game.getCurrentPitcher() != null ? game.getCurrentPitcher().getName() : "없음");
        }
        games.put(gameId, game); // 메모리 내 게임 상태 업데이트
    }

    @Override
    public void setComputerLineupAndPitcher(String gameId, String teamName, List<String> battingOrderPlayerNames, String startingPitcherName) {
        // 컴퓨터 팀의 라인업 설정도 동일한 유효성 검사 및 설정 로직을 따릅니다.
        setTeamLineupAndPitcher(gameId, teamName, battingOrderPlayerNames, startingPitcherName);
    }

    protected void checkCount(GameDto game) {
        if (game.getStrike() >= 3) {
            game.setOut(game.getOut() + 1);
            game.setStrike(0);
            game.setBall(0);
            advanceBattingOrder(game);
        }
        if (game.getBall() >= 4) {
            game.setBall(0);
            game.setStrike(0);

            // 모든 주자 1베이스 진루 (타자 본인 포함)
            GameLogicUtil.advanceRunners(game, 1);
            // 타자를 1루에 놓음 (advanceRunners는 이미 루상 주자를 처리했으므로, 타자만 새로 추가)
            GameLogicUtil.addRunnerToBase(game, 1, game.getCurrentBatter());
            
            advanceBattingOrder(game); // 4볼 볼넷 시 타자 변경
            logger.info("게임 {}: 4볼 볼넷! 타자 {} 1루 진루. 다음 타자: {}",
                game.getGameId(),
                game.getCurrentBatter() != null ? game.getCurrentBatter().getName() : "없음",
                game.getBattingOrder().get(game.getCurrentBatterIndex()).getName());
        }
        if (game.getOut() >= 3) {
            // 3아웃 시 이닝 종료 준비. 다음 이닝 처리는 nextInning 메서드에서 수행해야 합니다.
            logger.info("게임 {}: 3아웃, 이닝 종료 준비. 점수: {} {} : {} {}",
                game.getGameId(), game.getAwayTeam(), game.getAwayScore(), game.getHomeScore(), game.getHomeTeam());
        }
    }

    protected void advanceBattingOrder(GameDto game) {
        if (game.getBattingOrder() == null || game.getBattingOrder().isEmpty()) {
            logger.warn("게임 {}: 타순이 설정되지 않았습니다.", game.getGameId());
            return;
        }
        int nextIndex = (game.getCurrentBatterIndex() + 1) % game.getBattingOrder().size();
        game.setCurrentBatterIndex(nextIndex);
        game.setCurrentBatter(game.getBattingOrder().get(nextIndex));
        logger.info("게임 {}: 다음 타자: {} (타순 {})", game.getGameId(), game.getCurrentBatter().getName(), nextIndex + 1);
    }

    protected void checkGameOver(GameDto game) {
        // 게임이 아직 종료되지 않았을 때만 확인
        if (game.isGameOver()) {
            return;
        }

        // 정규 이닝(maxInning)의 말 공격이 3아웃으로 끝났을 때
        if (game.getInning() >= game.getMaxInning() && !game.isTop() && game.getOut() >= 3) {
            if (game.getHomeScore() != game.getAwayScore()) {
                endGame(game.getGameId()); // 점수 차이가 나면 게임 종료
            } else {
                // 동점이면 연장전 없이 무승부로 즉시 게임 종료
                logger.info("게임 {}: {}회 말 종료 동점. 연장전 없이 무승부 처리합니다.", game.getGameId(), game.getMaxInning());
                endGame(game.getGameId()); // 무승부로 게임 종료 호출
            }
        }
    }

    protected void handleScore(GameDto game, int score) {
        if (game.isTop()) { // 현재 이닝이 초(true)면 원정팀(어웨이) 득점
            game.setAwayScore(game.getAwayScore() + score);
        } else { // 현재 이닝이 말(false)이면 홈팀 득점
            game.setHomeScore(game.getHomeScore() + score);
        }
        logger.info("게임 {}: 점수 발생! 현재 점수: {} {} : {} {}",
            game.getGameId(), game.getAwayTeam(), game.getAwayScore(), game.getHomeScore(), game.getHomeTeam());
    }
}