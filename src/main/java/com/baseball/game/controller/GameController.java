package com.baseball.game.controller;

import com.baseball.game.dto.GameCreateRequest;
import com.baseball.game.dto.GameDto;
import com.baseball.game.dto.GamePlayView;
import com.baseball.game.dto.ApiResponse;
import com.baseball.game.dto.GameActionRequest;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.service.GameService;
import com.baseball.game.util.ValidationUtil;
import com.baseball.game.dto.TeamLineupSetRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Setter;

@RestController
@RequestMapping("/api/baseball")
@CrossOrigin(origins = "*")
public class GameController {

	private static final Logger log = LoggerFactory.getLogger(GameController.class);

	@Setter(onMethod_ = @Autowired)
	private GameService service;

	/**
	 * 게임 생성 (이닝 수, 사용자 공격 여부 포함)
	 * 요청 본문으로 홈팀, 원정팀, 최대 이닝, 사용자 공격 여부를 받아 게임을 생성합니다.
	 * 
	 * @param request 게임 생성 요청 DTO (홈팀, 원정팀, 최대 이닝, 사용자 공격 여부 포함)
	 * @return 성공 시 생성된 GameDto를 포함하는 ApiResponse
	 */
	@PostMapping("/game")
	public ApiResponse<GameDto> createGame(@RequestBody GameCreateRequest request) {
		// 유효성 검사
		ValidationUtil.validateGameCreateRequest(request);
		GameDto game = service.createGame(request.getHomeTeam(), request.getAwayTeam(), request.getMaxInning(),
				request.isIsUserOffense());
		// 사용자 ID를 GameDto에 저장 (MemberDto의 Id와 매핑)
		game.setUserId(request.getUserId());
		return ApiResponse.success(game, "게임이 생성되었습니다. ID: " + game.getGameId());
	}

	/**
	 * 게임 정보 조회
	 * 
	 * @param gameId 게임 ID
	 * @return 성공 시 게임 정보를 포함하는 ApiResponse
	 */
	@GetMapping("/game/{gameId}")
	public ApiResponse<GameDto> getGame(@PathVariable String gameId) {
		GameDto game = service.getGame(gameId);
		return ApiResponse.success(game);
	}

	/**
	 * 투구 (사용자 투수 턴)
	 * 
	 * @param gameId  게임 ID
	 * @param request 투구 요청 DTO (투구 유형 포함)
	 * @return 성공 시 결과 메시지와 업데이트된 게임 DTO를 포함하는 ApiResponse
	 */
	@PostMapping("/game/{gameId}/pitch")
	public ApiResponse<GamePlayView> pitcherThrow(@PathVariable String gameId, @RequestBody GameActionRequest request) {
		String pitchType = request.getPitchType();
		if (pitchType == null || pitchType.isEmpty()) {
			throw new ValidationException("투구 유형을 지정해주세요.");
		}

		String result = service.pitcherThrow(gameId, pitchType);
		GameDto game = service.getGame(gameId);
		GamePlayView view = GamePlayView.builder()
				.gameId(game.getGameId())
				.userId(game.getUserId())
				.homeTeam(game.getHomeTeam())
				.awayTeam(game.getAwayTeam())
				.inning(game.getInning())
				.isTop(game.isTop())
				.offenseTeam(game.getOffenseTeam())
				.defenseTeam(game.getDefenseTeam())
				.offenseSide(game.getOffenseSide())
				.out(game.getOut())
				.strike(game.getStrike())
				.ball(game.getBall())
				.homeScore(game.getHomeScore())
				.awayScore(game.getAwayScore())
				.currentBatter(game.getCurrentBatter())
				.currentPitcher(game.getCurrentPitcher())
				.bases(game.getBases())
				.build();

		return ApiResponse.success(view, "투구 처리 완료: " + result);
	}

	/**
	 * 타격 (사용자 타자 턴)
	 * 
	 * @param gameId  게임 ID
	 * @param request 타격 요청 DTO (스윙 여부, 타이밍 포함)
	 * @return 성공 시 결과 메시지와 업데이트된 게임 DTO를 포함하는 ApiResponse
	 */
	@PostMapping("/game/{gameId}/swing")
	public ApiResponse<GamePlayView> batterSwing(@PathVariable String gameId, @RequestBody GameActionRequest request) {
		Boolean Swing = request.getSwing();
		Double timing = request.getTiming();
		if (Swing == null || timing == null) {
			throw new ValidationException("스윙 여부와 타이밍을 지정해주세요.");
		}

		String result = service.batterSwing(gameId, Swing, timing);
		GameDto game = service.getGame(gameId);

		GamePlayView view = GamePlayView.builder()
				.gameId(game.getGameId())
				.userId(game.getUserId())
				.homeTeam(game.getHomeTeam())
				.awayTeam(game.getAwayTeam())
				.inning(game.getInning())
				.isTop(game.isTop())
				.offenseTeam(game.getOffenseTeam())
				.defenseTeam(game.getDefenseTeam())
				.offenseSide(game.getOffenseSide())
				.out(game.getOut())
				.strike(game.getStrike())
				.ball(game.getBall())
				.homeScore(game.getHomeScore())
				.awayScore(game.getAwayScore())
				.currentBatter(game.getCurrentBatter())
				.currentPitcher(game.getCurrentPitcher())
				.baseRunners(game.getBaseRunners())
				.bases(game.getBases())
				.build();

		return ApiResponse.success(view, "스윙/노스윙 처리 완료: " + result);
	}

	/**
	 * 컴퓨터의 턴을 진행합니다.
	 * 게임 상태(공격/수비)에 따라 컴퓨터가 투수 또는 타자 역할을 수행하고 결과를 반환합니다.
	 * 
	 * @param gameId 게임 ID
	 * @return 성공 시 결과 메시지와 업데이트된 게임 DTO를 포함하는 ApiResponse
	 */
	@PostMapping("/game/{gameId}/computer-turn")
	public ApiResponse<GameDto> playComputerTurn(@PathVariable String gameId) {
		String result = service.playComputerTurn(gameId);
		GameDto game = service.getGame(gameId);

		return ApiResponse.success(game, "컴퓨터의 턴이 성공적으로 진행되었습니다: " + result);
	}

	/**
	 * 다음 이닝으로 진행
	 * 
	 * @param gameId 게임 ID
	 * @return 성공 시 업데이트된 게임 DTO를 포함하는 ApiResponse
	 */
	@PostMapping("/game/{gameId}/next-inning")
	public ApiResponse<GameDto> nextInning(@PathVariable String gameId) {
		GameDto game = service.nextInning(gameId);
		return ApiResponse.success(game, "다음 이닝으로 진행되었습니다.");
	}

	/**
	 * 게임 리셋
	 * 
	 * @param gameId 게임 ID
	 * @return 성공 시 업데이트된 게임 DTO를 포함하는 ApiResponse
	 */
	@PostMapping("/game/{gameId}/reset")
	public ApiResponse<GameDto> resetGame(@PathVariable String gameId) {
		service.resetGame(gameId);
		GameDto game = service.getGame(gameId);
		return ApiResponse.success(game, "게임이 성공적으로 리셋되었습니다.");
	}

	/**
	 * 특정 게임에 팀 라인업/선발 투수를 적용
	 * - teamName: 홈/원정팀 중 하나
	 * - battingOrder: 타자 9명 이름(타순 순서)
	 * - startingPitcher: 선발 투수 이름
	 */
	@PostMapping("/game/{gameId}/lineup")
	public ApiResponse<GameDto> applyTeamLineup(
			@PathVariable String gameId,
			@RequestBody TeamLineupSetRequest request) {
		ValidationUtil.validateGameId(gameId);
		ValidationUtil.validateTeamLineupSetRequest(request);
		GameDto game = service.applyTeamLineup(gameId, request);
		return ApiResponse.success(game, "라인업이 적용되었습니다.");
	}
}