package com.baseball.game.service;
import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.Batter;
import com.baseball.game.dto.Pitcher;
import com.baseball.game.util.GameLogicUtil;
import com.baseball.game.exception.GameNotFoundException;
import com.baseball.game.exception.InvalidGameStateException;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.Setter;

@Service
@Transactional
public class GameServiceImpl implements GameService {
	private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
	
	@Setter(onMethod_ = @Autowired)
	private GameRepository gameRepository;
	
	
	// 메모리 기반 (임시, Redis 연동 후 제거 예정)
	private Map<String, GameDto> games = new HashMap<>();
	
	@Override
	@Transactional
	public GameDto createGame(String homeTeam, String awayTeam) {
		// 팀 검증
		if (homeTeam == null || awayTeam == null) {
			throw new ValidationException("홈팀과 원정팀 모두 필수입니다.");
		}
		
		if (homeTeam.trim().equalsIgnoreCase(awayTeam.trim())) {
			throw new ValidationException("홈팀과 원정팀은 서로 다른 팀이어야 합니다.");
		}
		
		GameDto game = new GameDto();
		game.setGameId(UUID.randomUUID().toString());
		game.setHomeTeam(homeTeam);
		game.setAwayTeam(awayTeam);
		
		Batter defaultBatter = new Batter();
		defaultBatter.setName("기본타자");
		defaultBatter.setTeam(homeTeam);
		defaultBatter.setPower(60);
		defaultBatter.setContact(70);
		defaultBatter.setSpeed(50);
		defaultBatter.setEye(60);
		
		Pitcher defaultPitcher = new Pitcher();
		defaultPitcher.setName("기본투수");
		defaultPitcher.setTeam(awayTeam);
		defaultPitcher.setControl(70);
		defaultPitcher.setSpeed(80);
		defaultPitcher.setStamina(60);
		defaultPitcher.setMovement(65);
		
		game.setCurrentBatter(defaultBatter);
		game.setCurrentPitcher(defaultPitcher);
		game.setIsUserOffense(true); // 유저가 먼저 공격
		
		// Redis에 저장 (향후 메모리 기반 제거 시 활성화)
		// gameRepository.save(game);
		
		// 임시로 메모리에 저장
		games.put(game.getGameId(), game);
		
		logger.info("게임 생성 완료: gameId={}, homeTeam={}, awayTeam={}", 
			game.getGameId(), homeTeam, awayTeam);
		
		return game;
	}
	
	@Override
	public GameDto getGame(String gameId) {
		if (gameId == null || gameId.trim().isEmpty()) {
			throw new ValidationException("게임 ID는 필수입니다.");
		}
		
		GameDto game = games.get(gameId);
		if (game == null) {
			throw new GameNotFoundException(gameId);
		}
		
		return game;
	}

	// 싱글플레이: 유저/컴퓨터 번갈아 공격/수비
	public String userAction(String gameId, Boolean swing, String pitchType) {
		GameDto game = getGame(gameId); // 예외 처리된 getGame 사용
		
		// 게임이 이미 종료되었는지 확인
		if (game.isGameOver()) {
			throw new InvalidGameStateException("이미 종료된 게임입니다.");
		}
		StringBuilder result = new StringBuilder();
		if (game.isIsUserOffense()) {
			// 유저가 타자, 컴퓨터가 투수
			if (swing == null) return "스윙 여부를 입력하세요.";
			String computerPitch = getRandomPitchType();
			result.append("[컴퓨터 투수: ").append(computerPitch).append("] ");
			String playResult = processAtBat(gameId, swing, computerPitch);
			result.append(playResult);
			if (isTurnChange(game)) {
				game.setIsUserOffense(false);
				result.append(" | 턴 전환: 유저 → 컴퓨터");
			}
		} else {
			// 유저가 투수, 컴퓨터가 타자
			if (pitchType == null) return "투구 타입을 입력하세요.";
			boolean computerSwing = getRandomSwing();
			result.append("[컴퓨터 타자: ").append(computerSwing ? "스윙" : "노스윙").append("] ");
			String playResult = processAtBat(gameId, computerSwing, pitchType);
			result.append(playResult);
			if (isTurnChange(game)) {
				game.setIsUserOffense(true);
				result.append(" | 턴 전환: 컴퓨터 → 유저");
			}
		}
		return result.toString();
	}

	private boolean isTurnChange(GameDto game) {
		return game.getOut() >= 3;
	}

	private String getRandomPitchType() {
		return Math.random() < 0.5 ? "strike" : "ball";
	}

	private boolean getRandomSwing() {
		return Math.random() < 0.7;
	}

	// 기존 메서드들은 싱글플레이용 userAction으로 대체(호환용)
	@Override
	public String batterSwing(String gameId, boolean swing) {
		return userAction(gameId, swing, null);
	}
	
	@Override
	@Transactional
	public String batterSwing(String gameId, boolean swing, double timing) {
		GameDto game = getGame(gameId); // 예외 처리된 getGame 사용
		
		// 게임이 이미 종료되었는지 확인
		if (game.isGameOver()) {
			throw new InvalidGameStateException("이미 종료된 게임입니다.");
		}
		
		// 현재 턴이 타자 턴인지 확인
		if (!game.isIsUserOffense()) {
			throw new InvalidGameStateException("현재는 투수 턴입니다.");
		}
		
		if (!swing) {
			return "노 스윙";
		}
		
		// 타이밍을 반영한 타격 결과 결정
		String result = GameLogicUtil.determineHitResultWithTiming(swing, game.getCurrentPitcher(), "strike", timing, game.getCurrentBatter());
		
		// 결과에 따른 게임 상태 업데이트
		switch (result) {
			case "홈런":
				game.setHomeScore(game.getHomeScore() + 1);
				GameLogicUtil.advanceRunners(game, 4);
				break;
			case "3루타":
				GameLogicUtil.addRunnerToBase(game, 3, game.getCurrentBatter());
				GameLogicUtil.advanceRunners(game, 3);
				break;
			case "2루타":
				GameLogicUtil.addRunnerToBase(game, 2, game.getCurrentBatter());
				GameLogicUtil.advanceRunners(game, 2);
				break;
			case "안타":
				GameLogicUtil.addRunnerToBase(game, 1, game.getCurrentBatter());
				GameLogicUtil.advanceRunners(game, 1);
				break;
			case "헛스윙":
				game.setStrike(game.getStrike() + 1);
				break;
			case "땅볼 아웃":
			case "뜬공 아웃":
				game.setOut(game.getOut() + 1);
				break;
		}
		
		checkCount(game);
		checkGameOver(game);

		// 타순 순환
		advanceBattingOrder(game);

		// Redis에 업데이트된 게임 상태 저장 (향후 활성화)
		// gameRepository.save(game);

		logger.info("타격 처리 완료: gameId={}, result={}, score={}-{}", 
			gameId, result, game.getHomeScore(), game.getAwayScore());

		return result;
	}
	@Override
	public String pitcherThrow(String gameId, String pitchType) {
		return userAction(gameId, null, pitchType);
	}
	@Override
	public String processAtBat(String gameId) {
		return userAction(gameId, true, null);
	}
	public String processAtBat(String gameId, boolean swing, String pitchType) {
		GameDto game = getGame(gameId); // 예외 처리된 getGame 사용
		
		// 게임이 이미 종료되었는지 확인
		if (game.isGameOver()) {
			throw new InvalidGameStateException("이미 종료된 게임입니다.");
		}
		
		Batter batter = game.getCurrentBatter();
		Pitcher pitcher = game.getCurrentPitcher();
		if (batter == null || pitcher == null) {
			throw new InvalidGameStateException("선수 정보가 없습니다.");
		}
		String result = GameLogicUtil.determineHitResult(swing, pitcher, pitchType != null ? pitchType : "strike");
		if (!swing) {
			if (result.equals("스트라이크")) {
				game.setStrike(game.getStrike() + 1);
			} else {
				game.setBall(game.getBall() + 1);
			}
			checkCount(game);
			return "노 스윙 (" + result + ")";
		}
		if (result.equals("땅볼")) {
			String groundResult = GameLogicUtil.processGroundBall(game, batter);
			checkCount(game);
			checkGameOver(game);
			return groundResult;
		}
		switch (result) {
			case "홈런":
				game.setHomeScore(game.getHomeScore() + 1);
				GameLogicUtil.advanceRunners(game, 4);
				break;
			case "3루타":
				GameLogicUtil.addRunnerToBase(game, 3, game.getCurrentBatter());
				GameLogicUtil.advanceRunners(game, 3);
				break;
			case "2루타":
				GameLogicUtil.addRunnerToBase(game, 2, game.getCurrentBatter());
				GameLogicUtil.advanceRunners(game, 2);
				break;
			case "안타":
				GameLogicUtil.addRunnerToBase(game, 1, game.getCurrentBatter());
				GameLogicUtil.advanceRunners(game, 1);
				break;
			default:
				game.setOut(game.getOut() + 1);
				break;
		}
		checkCount(game);
		checkGameOver(game);
		return result;
	}
	@Override
	public GameDto nextInning(String gameId) {
		GameDto game = getGame(gameId); // 예외 처리된 getGame 사용
		
		// 게임이 이미 종료되었는지 확인
		if (game.isGameOver()) {
			throw new InvalidGameStateException("이미 종료된 게임입니다.");
		}
		if (game.isTop()) {
			game.setTop(false);
		} else {
			game.setInning(game.getInning() + 1);
			game.setTop(true);
		}
		GameLogicUtil.resetBases(game);
		game.setOut(0);
		game.setStrike(0);
		game.setBall(0);
		// 이닝 교대 시 턴도 교대
		game.setIsUserOffense(!game.isIsUserOffense());
		checkGameOver(game);
		return game;
	}
	@Override
	@Transactional
	public GameDto endGame(String gameId) {
		GameDto game = getGame(gameId); // 예외 처리된 getGame 사용
		
		// 이미 종료된 게임인지 확인
		if (game.isGameOver()) {
			throw new InvalidGameStateException("이미 종료된 게임입니다.");
		}
		
		game.setGameOver(true);
		if (game.getHomeScore() > game.getAwayScore()) {
			game.setWinner(game.getHomeTeam());
		} else if (game.getAwayScore() > game.getHomeScore()) {
			game.setWinner(game.getAwayTeam());
		} else {
			game.setWinner("무승부");
		}
		
		// Redis에 최종 게임 상태 저장 (향후 활성화)
		// gameRepository.save(game);
		
		logger.info("게임 종료: gameId={}, winner={}, finalScore={}-{}", 
			gameId, game.getWinner(), game.getHomeScore(), game.getAwayScore());
		
		return game;
	}
	@Override
	public void advanceRunners(String gameId, int bases) {
		GameDto game = getGame(gameId); // 예외 처리된 getGame 사용
		
		// 게임이 이미 종료되었는지 확인
		if (game.isGameOver()) {
			throw new InvalidGameStateException("이미 종료된 게임입니다.");
		}
		
		GameLogicUtil.advanceRunners(game, bases);
	}

	@Override
	public String getGameStats(String gameId) {
		GameDto game = getGame(gameId); // 예외 처리된 getGame 사용
		StringBuilder stats = new StringBuilder();
		stats.append("=== 게임 통계 ===\n");
		stats.append(String.format("홈팀: %s (%d점)\n", game.getHomeTeam(), game.getHomeScore()));
		stats.append(String.format("원정팀: %s (%d점)\n", game.getAwayTeam(), game.getAwayScore()));
		stats.append(String.format("이닝: %d%s\n", game.getInning(), game.isTop() ? "초" : "말"));
		stats.append(String.format("아웃: %d, 스트라이크: %d, 볼: %d\n", game.getOut(), game.getStrike(), game.getBall()));
		stats.append(String.format("현재 턴: %s\n", game.isIsUserOffense() ? "유저(타자)" : "컴퓨터(타자)"));
		if (game.getCurrentBatter() != null) {
			stats.append(String.format("현재 타자: %s\n", game.getCurrentBatter().getName()));
		}
		if (game.getCurrentPitcher() != null) {
			stats.append(String.format("현재 투수: %s\n", game.getCurrentPitcher().getName()));
		}
		if (game.isGameOver()) {
			stats.append(String.format("게임 종료! 승자: %s\n", game.getWinner()));
		}
		return stats.toString();
	}
	private void checkCount(GameDto game) {
		if (game.getStrike() >= 3) {
			game.setOut(game.getOut() + 1);
			game.setStrike(0);
			game.setBall(0);
		}
		if (game.getBall() >= 4) {
			game.setBall(0);
			game.setStrike(0);
			if (game.getCurrentBatter() != null) {
				GameLogicUtil.addRunnerToBase(game, 1, game.getCurrentBatter());
			}
		}
		if (game.getOut() >= 3) {
			nextInning(game.getGameId());
		}
	}
	private void checkGameOver(GameDto game) {
		if (game.getInning() > game.getMaxInning() && !game.isTop()) {
			endGame(game.getGameId());
		}
	}

	private void advanceBattingOrder(GameDto game) {
		if (game.getBattingOrder() == null || game.getBattingOrder().isEmpty()) return;
		int nextIndex = (game.getCurrentBatterIndex() + 1) % game.getBattingOrder().size();
		game.setCurrentBatter(game.getBattingOrder().get(nextIndex));
		game.setCurrentBatterIndex(nextIndex);
	}
}
