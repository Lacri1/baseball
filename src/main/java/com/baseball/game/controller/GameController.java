package com.baseball.game.controller;

import com.baseball.game.dto.GameCreateRequest;
import com.baseball.game.dto.GameDto;
import com.baseball.game.service.GameService;
import com.baseball.game.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Setter;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/baseball")
@CrossOrigin(origins = "*")
public class GameController {

	private static final Logger logger = LoggerFactory.getLogger(GameController.class);

	@Setter(onMethod_ = @Autowired)
	private GameService service;

	// 게임 생성 (이닝 수 포함)
	@PostMapping("/game")
	public GameDto createGame(@RequestBody GameCreateRequest request) {
		logger.info("게임 생성 요청: 홈팀={}, 원정팀={}, 이닝={}", new Object[]{request.getHomeTeam(), request.getAwayTeam(), request.getMaxInning()});

		// 입력값 검증
		ValidationUtil.validateTeamName(request.getHomeTeam());
		ValidationUtil.validateTeamName(request.getAwayTeam());
		ValidationUtil.validateDifferentTeams(request.getHomeTeam(), request.getAwayTeam());
		ValidationUtil.validateMaxInning(request.getMaxInning());

		String homeTeam, awayTeam;
		if (request.isIsUserOffense()) {
			awayTeam = request.getHomeTeam();
			homeTeam = request.getAwayTeam();
		} else {
			homeTeam = request.getHomeTeam();
			awayTeam = request.getAwayTeam();
		}
		GameDto game = service.createGame(request.getHomeTeam(), request.getAwayTeam());
		game.setMaxInning(request.getMaxInning()); // 이닝 수 저장

		logger.info("게임 생성 완료: gameId={}", game.getGameId());
		return game;
	}

	// 게임 정보 조회
	@GetMapping("/game/{gameId}")
	public Map<String, Object> getGame(@PathVariable String gameId) {
		logger.info("게임 조회 요청: gameId={}", gameId);

		// 입력값 검증
		ValidationUtil.validateGameId(gameId);

		Map<String, Object> response = new HashMap<>();
		GameDto game = service.getGame(gameId);
		response.put("success", true);
		response.put("game", game);

		logger.info("게임 조회 완료: gameId={}", gameId);
		return response;
	}

	// 타격
	@PostMapping("/game/{gameId}/batter")
	public Map<String, Object> batterSwing(@PathVariable String gameId, @RequestBody Map<String, Object> request) {
		logger.info("타격 요청: gameId={}, swing={}, timing={}",
				new Object[] {gameId, request.get("swing"), request.get("timing")});

		// 입력값 검증
		ValidationUtil.validateGameId(gameId);
		ValidationUtil.validateSwing((Boolean) request.get("swing"));

		Double timing = (Double) request.get("timing");
		if (timing == null) {
			timing = 0.5; // 기본값
		}
		ValidationUtil.validateTiming(timing);

		Map<String, Object> response = new HashMap<>();
		String result = service.batterSwing(gameId, (Boolean) request.get("swing"), timing);
		GameDto game = service.getGame(gameId);

		response.put("success", true);
		response.put("result", result);
		response.put("game", game);

		logger.info("타격 처리 완료: gameId={}, result={}", gameId, result);
		return response;
	}

	// 투구
	@PostMapping("/game/{gameId}/pitcher")
	public Map<String, Object> pitcherThrow(@PathVariable String gameId, @RequestBody Map<String, String> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			String pitchType = request.get("pitchType");
			if (pitchType == null) {
				response.put("success", false);
				response.put("message", "투구 타입을 지정해주세요.");
				return response;
			}

			String result = service.pitcherThrow(gameId, pitchType);
			GameDto game = service.getGame(gameId);

			response.put("success", true);
			response.put("result", result);
			response.put("game", game);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "투구 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}

	// 다음 이닝
	@PostMapping("/game/{gameId}/next-inning")
	public Map<String, Object> nextInning(@PathVariable String gameId) {
		Map<String, Object> response = new HashMap<>();
		try {
			GameDto game = service.nextInning(gameId);
			if (game == null) {
				response.put("success", false);
				response.put("message", "게임을 찾을 수 없습니다.");
			} else {
				response.put("success", true);
				response.put("game", game);
				response.put("message", "다음 이닝으로 진행됩니다.");
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "이닝 진행 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}

	// 게임 종료
	@PostMapping("/game/{gameId}/end")
	public Map<String, Object> endGame(@PathVariable String gameId) {
		Map<String, Object> response = new HashMap<>();
		try {
			GameDto game = service.endGame(gameId);
			if (game == null) {
				response.put("success", false);
				response.put("message", "게임을 찾을 수 없습니다.");
			} else {
				response.put("success", true);
				response.put("game", game);
				response.put("message", "게임이 종료되었습니다. 승자: " + game.getWinner());
			}
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "게임 종료 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}

	// 베이스 러닝
	@PostMapping("/game/{gameId}/advance-runners")
	public Map<String, Object> advanceRunners(@PathVariable String gameId, @RequestBody Map<String, Integer> request) {
		Map<String, Object> response = new HashMap<>();
		try {
			Integer bases = request.get("bases");
			if (bases == null) {
				response.put("success", false);
				response.put("message", "진루할 베이스 수를 지정해주세요.");
				return response;
			}

			service.advanceRunners(gameId, bases);
			GameDto game = service.getGame(gameId);

			response.put("success", true);
			response.put("game", game);
			response.put("message", bases + "베이스 진루했습니다.");
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "베이스 러닝 처리 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}

	// 게임 통계 조회
	@GetMapping("/game/{gameId}/stats")
	public Map<String, Object> getGameStats(@PathVariable String gameId) {
		Map<String, Object> response = new HashMap<>();
		try {
			String stats = service.getGameStats(gameId);
			response.put("success", true);
			response.put("stats", stats);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "게임 통계 조회 중 오류가 발생했습니다: " + e.getMessage());
		}
		return response;
	}
}
