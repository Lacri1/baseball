package com.baseball.game.controller;

import com.baseball.game.dto.LineupRequest;
import com.baseball.game.dto.GameCreateRequest;
import com.baseball.game.dto.GameDto;
import com.baseball.game.service.GameService;
import com.baseball.game.dto.ApiResponse;
import com.baseball.game.dto.GameActionRequest;
import com.baseball.game.util.ValidationUtil;
import com.baseball.game.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Setter;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/baseball")
@CrossOrigin(origins = "*")
public class GameController {

	private static final Logger logger = LoggerFactory.getLogger(GameController.class);

	@Setter(onMethod_ = @Autowired)
	private GameService service;

	// 게임 생성 (이닝 수 포함)
	@PostMapping("/game")
	public ResponseEntity<ApiResponse<GameDto>> createGame(@Valid @RequestBody GameCreateRequest request) {
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
		GameDto game = service.createGame(request.getHomeTeam(), request.getAwayTeam(), request.getMaxInning(), request.isIsUserOffense());

		logger.info("게임 생성 완료: gameId={}", game.getGameId());
		return ResponseEntity.ok(ApiResponse.success(game, "게임이 생성되었습니다. ID: " + game.getGameId()));
	}

	// 게임 정보 조회
	@GetMapping("/game/{gameId}")
	public ResponseEntity<ApiResponse<GameDto>> getGame(@PathVariable String gameId) {
		logger.info("게임 조회 요청: gameId={}", gameId);

		// 입력값 검증
		ValidationUtil.validateGameId(gameId);

		GameDto game = service.getGame(gameId);
		logger.info("게임 조회 완료: gameId={}", gameId);
		return ResponseEntity.ok(ApiResponse.success(game));
	}

	// 타격
	@PostMapping("/game/{gameId}/batter")
	public ResponseEntity<ApiResponse<Map<String, Object>>> batterSwing(@PathVariable String gameId, @Valid @RequestBody GameActionRequest request) {
		logger.info("타격 요청: gameId={}, swing={}, timing={}",
				new Object[] {gameId, request.getSwing(), request.getTiming()});

		// 입력값 검증
		ValidationUtil.validateGameId(gameId);
		ValidationUtil.validateSwing(request.getSwing());

		Double timing = request.getTiming();
		if (timing == null) {
			timing = 0.5; // 기본값
		}
		ValidationUtil.validateTiming(timing);

		String result = service.batterSwing(gameId, request.getSwing(), timing);
		GameDto game = service.getGame(gameId);

		Map<String, Object> data = new HashMap<>();
		data.put("result", result);
		data.put("game", game);
		data.put("currentBatter", game.getCurrentBatter());
		data.put("currentPitcher", game.getCurrentPitcher());
		data.put("offenseTeam", game.getOffenseTeam());
		data.put("offenseSide", game.getOffenseSide());

		logger.info("타격 처리 완료: gameId={}, result={}", gameId, result);
		return ResponseEntity.ok(ApiResponse.success(data, "스윙/노스윙 처리 완료: " + result));
	}

	// 투구
	@PostMapping("/game/{gameId}/pitcher")
		public ResponseEntity<ApiResponse<Map<String, Object>>> pitcherThrow(@PathVariable String gameId, @Valid @RequestBody GameActionRequest request) {
		try {
			String pitchType = request.getPitchType();

			String result = service.pitcherThrow(gameId, pitchType);
			GameDto game = service.getGame(gameId);

			Map<String, Object> data = new HashMap<>();
			data.put("result", result);
			data.put("game", game);
			data.put("currentBatter", game.getCurrentBatter());
			data.put("currentPitcher", game.getCurrentPitcher());
			data.put("offenseTeam", game.getOffenseTeam());
			data.put("offenseSide", game.getOffenseSide());

			return ResponseEntity.ok(ApiResponse.success(data, "투구 처리 완료: " + result));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("투구 처리 중 오류가 발생했습니다: " + e.getMessage(), "PITCH_ERROR"));
		}
	}

	// 다음 이닝
	@PostMapping("/game/{gameId}/next-inning")
	public ResponseEntity<ApiResponse<GameDto>> nextInning(@PathVariable String gameId) {
		try {
			GameDto game = service.nextInning(gameId);
			if (game == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error("게임을 찾을 수 없습니다.", "GAME_NOT_FOUND"));
			} else {
				return ResponseEntity.ok(ApiResponse.success(game, "다음 이닝으로 진행됩니다."));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("이닝 진행 중 오류가 발생했습니다: " + e.getMessage(), "NEXT_INNING_ERROR"));
		}
	}

	// 게임 종료
	@PostMapping("/game/{gameId}/end")
	public ResponseEntity<ApiResponse<GameDto>> endGame(@PathVariable String gameId) {
		try {
			GameDto game = service.endGame(gameId);
			if (game == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body(ApiResponse.error("게임을 찾을 수 없습니다.", "GAME_NOT_FOUND"));
			} else {
				return ResponseEntity.ok(ApiResponse.success(game, "게임이 종료되었습니다. 승자: " + game.getWinner()));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("게임 종료 중 오류가 발생했습니다: " + e.getMessage(), "END_GAME_ERROR"));
		}
	}

	// 베이스 러닝
	@PostMapping("/game/{gameId}/advance-runners")
	public ResponseEntity<ApiResponse<GameDto>> advanceRunners(@PathVariable String gameId, @Valid @RequestBody Map<String, Integer> request) {
		try {
			Integer bases = request.get("bases");
			if (bases == null) {
				throw new ValidationException("진루할 베이스 수를 지정해주세요.");
			}

			service.advanceRunners(gameId, bases);
			GameDto game = service.getGame(gameId);

			return ResponseEntity.ok(ApiResponse.success(game, bases + "베이스 진루했습니다."));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("베이스 러닝 처리 중 오류가 발생했습니다: " + e.getMessage(), "ADVANCE_RUNNERS_ERROR"));
		}
	}

	// 게임 통계 조회
	@GetMapping("/game/{gameId}/stats")
	public ResponseEntity<ApiResponse<String>> getGameStats(@PathVariable String gameId) {
		try {
			String stats = service.getGameStats(gameId);
			return ResponseEntity.ok(ApiResponse.success(stats));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("게임 통계 조회 중 오류가 발생했습니다: " + e.getMessage(), "GET_STATS_ERROR"));
		}
	}

	// 라인업 설정
	@PostMapping("/game/{gameId}/lineup")
	public ResponseEntity<ApiResponse<Void>> setLineup(@PathVariable String gameId, @RequestBody LineupRequest request) {
		logger.info("라인업 설정 요청: gameId={}, teamName={}", gameId, request.getTeamName());
		service.setLineup(gameId, request);
		return ResponseEntity.ok(ApiResponse.success(null, "라인업이 성공적으로 설정되었습니다."));
	}
}
