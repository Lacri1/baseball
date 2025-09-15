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
import com.baseball.game.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Setter;
import com.baseball.game.dto.BatterGameStats;
import com.baseball.game.dto.PitcherGameStats;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Comparator;

@RestController
@RequestMapping(value = "/api/baseball", produces = "application/json; charset=UTF-8")
@CrossOrigin(origins = "*")
public class GameController {

	private static final Logger log = LoggerFactory.getLogger(GameController.class);

	@Setter(onMethod_ = @Autowired)
	private GameService service;

	/**
	 * 스코어보드: 이닝별 득점 + 합계 정보를 제공
	 */
	@GetMapping(value = "/game/{gameId}/scoreboard")
	public ApiResponse<com.baseball.game.dto.ScoreboardDto> getScoreboard(@PathVariable String gameId) {
		GameDto game = service.getGame(gameId);
		com.baseball.game.dto.ScoreboardDto view = buildScoreboardView(game);
		return ApiResponse.success(view);
	}

	@GetMapping(value = "/game/{gameId}/team-stats")
	public ApiResponse<java.util.Map<String, Object>> getTeamStats(@PathVariable String gameId) {
		GameDto game = service.getGame(gameId);
		java.util.Map<String, Object> resp = new java.util.HashMap<>();
		resp.put("homeTeam", game.getHomeTeam());
		resp.put("awayTeam", game.getAwayTeam());
		resp.put("homeScore", game.getHomeScore());
		resp.put("awayScore", game.getAwayScore());
		resp.put("homeHit", game.getHomeHit());
		resp.put("awayHit", game.getAwayHit());
		resp.put("homeWalks", game.getHomeWalks());
		resp.put("awayWalks", game.getAwayWalks());
		return ApiResponse.success(resp);
	}

	/**
	 * 게임 생성 (이닝 수, 사용자 공격 여부 포함)
	 * 요청 본문으로 홈팀, 원정팀, 최대 이닝, 사용자 공격 여부를 받아 게임을 생성합니다.
	 * 
	 * @param request 게임 생성 요청 DTO (홈팀, 원정팀, 최대 이닝, 사용자 공격 여부 포함)
	 * @return 성공 시 생성된 GameDto를 포함하는 ApiResponse
	 */
	    @PostMapping(value = "/game")
    public ApiResponse<GameDto> createGame(@RequestBody GameCreateRequest request) {
        // 유효성 검사
        ValidationUtil.validateGameCreateRequest(request);
        GameDto game = service.createGame(request.getHomeTeam(), request.getAwayTeam(), request.getMaxInning(),
                request.isIsUserOffense());
        // 사용자 ID를 GameDto에 저장 (MemberDto의 Id와 매핑)
        game.setUserId(request.getUserId());
        return ApiResponse.success(game, "게임이 생성되었습니다. ID: " + game.getGameId());
    }

    @GetMapping(value = "/games")
    public ApiResponse<java.util.List<GameDto>> getAllGames() {
        return ApiResponse.success(service.getAllGames());
    }

	/**
	 * 게임 정보 조회
	 * 
	 * @param gameId 게임 ID
	 * @return 성공 시 게임 정보를 포함하는 ApiResponse
	 */
	@GetMapping(value = "/game/{gameId}")
	public ApiResponse<GameDto> getGame(@PathVariable String gameId) {
		GameDto game = service.getGame(gameId);
		// 응답 직전에 타자/투수 스탯 맵을 팀/타순 기준으로 정렬
		sortStatsMapsForResponse(game);
		return ApiResponse.success(game);
	}

	/**
	 * 경량 게임 뷰: 중복 필드를 제거한 표시용 응답
	 */
	@GetMapping(value = "/game/{gameId}/view")
	public ApiResponse<GamePlayView> getGameView(@PathVariable String gameId) {
		GameDto game = service.getGame(gameId);
		GamePlayView view = buildGamePlayView(game);
		return ApiResponse.success(view);
	}

	/**
	 * 투구 (사용자 투수 턴)
	 * 
	 * @param gameId  게임 ID
	 * @param request 투구 요청 DTO (투구 유형 포함)
	 * @return 성공 시 결과 메시지와 업데이트된 게임 DTO를 포함하는 ApiResponse
	 */
	@PostMapping(value = "/game/{gameId}/pitch")
	public ApiResponse<GamePlayView> pitcherThrow(@PathVariable String gameId, @RequestBody GameActionRequest request) {
		String pitchType = request.getPitchType();
		if (pitchType == null || pitchType.isEmpty()) {
			throw new ValidationException("투구 유형을 지정해주세요.");
		}

		String result = service.pitcherThrow(gameId, pitchType);
		GameDto game = service.getGame(gameId);
		GamePlayView view = buildGamePlayView(game);

		return ApiResponse.success(view, "투구 처리 완료: " + result);
	}

	/**
	 * 타격 (사용자 타자 턴)
	 * 
	 * @param gameId  게임 ID
	 * @param request 타격 요청 DTO (스윙 여부, 타이밍 포함)
	 * @return 성공 시 결과 메시지와 업데이트된 게임 DTO를 포함하는 ApiResponse
	 */
	@PostMapping(value = "/game/{gameId}/swing")
	public ApiResponse<GamePlayView> batterSwing(@PathVariable String gameId, @RequestBody GameActionRequest request) {
		Boolean swing = request.getSwing();
		Boolean timing = request.getTiming();
		if (swing == null || timing == null) {
			throw new ValidationException("스윙 여부와 타이밍 보너스 사용 여부를 지정해주세요.");
		}

		String result = service.batterSwing(gameId, swing, timing);
		GameDto game = service.getGame(gameId);
		GamePlayView view = buildGamePlayView(game);

		return ApiResponse.success(view, "스윙/노스윙 처리 완료: " + result);
	}

	/**
	 * 특정 게임에 팀 라인업/선발 투수를 적용
	 * - teamName: 홈/원정팀 중 하나
	 * - battingOrder: 타자 9명 이름(타순 순서)
	 * - startingPitcher: 선발 투수 이름
	 */
	@PostMapping(value = "/game/{gameId}/lineup")
	public ApiResponse<GameDto> applyTeamLineup(
			@PathVariable String gameId,
			@RequestBody TeamLineupSetRequest request) {
		ValidationUtil.validateGameId(gameId);
		ValidationUtil.validateTeamLineupSetRequest(request);
		GameDto game = service.applyTeamLineup(gameId, request);
		return ApiResponse.success(game, "라인업이 적용되었습니다.");
	}

	/** 현재 경기 타자 스탯 조회 (없으면 0으로 초기화하여 반환) */
	@GetMapping(value = "/game/{gameId}/stats/batter/{playerName}")
	public ApiResponse<BatterGameStats> getBatterStats(
			@PathVariable String gameId,
			@PathVariable String playerName) {
		ValidationUtil.validateGameId(gameId);
		final String decodedPlayerName = ensureUtf8PlayerName(playerName);
		GameDto game = service.getGame(gameId);
		// 경기 라인업 포함 여부 검증 (홈/원정 타순 모두 확인)
		List<com.baseball.game.dto.Batter> home = game.getHomeBattingOrder();
		List<com.baseball.game.dto.Batter> away = game.getAwayBattingOrder();
		boolean inHome = home != null
				&& home.stream().anyMatch(b -> b != null && decodedPlayerName.equals(b.getName()));
		boolean inAway = away != null
				&& away.stream().anyMatch(b -> b != null && decodedPlayerName.equals(b.getName()));
		boolean inGame = inHome || inAway;
		if (!inGame) {
			return ApiResponse.error("해당 경기 라인업에 없는 타자입니다.", "NOT_IN_GAME");
		}
		BatterGameStats stats = null;
		if (game.getBatterGameStatsMap() != null) {
			stats = game.getBatterGameStatsMap().get(decodedPlayerName);
		}
		if (stats == null) {
			stats = BatterGameStats.builder()
					.playerName(decodedPlayerName)
					.plateAppearances(0)
					.atBats(0)
					.hits(0)
					.homeRuns(0)
					.walks(0)
					.strikeouts(0)
					.rbis(0)
					.build();
		}
		return ApiResponse.success(stats);
	}

	/** 현재 경기 투수 스탯 조회 (없으면 0으로 초기화하여 반환) */
	@GetMapping(value = "/game/{gameId}/stats/pitcher/{playerName}")
	public ApiResponse<PitcherGameStats> getPitcherStats(
			@PathVariable String gameId,
			@PathVariable String playerName) {
		ValidationUtil.validateGameId(gameId);
		final String decodedPlayerName = ensureUtf8PlayerName(playerName);
		GameDto game = service.getGame(gameId);
		// 경기 투입 투수 검증 (선발 둘 중 한 명만 허용)
		com.baseball.game.dto.Pitcher homeSP = game.getHomeStartingPitcher();
		com.baseball.game.dto.Pitcher awaySP = game.getAwayStartingPitcher();
		boolean isHomeSp = homeSP != null && decodedPlayerName.equals(homeSP.getName());
		boolean isAwaySp = awaySP != null && decodedPlayerName.equals(awaySP.getName());
		if (!isHomeSp && !isAwaySp) {
			return ApiResponse.error("해당 경기 투수가 아닙니다.", "NOT_IN_GAME");
		}
		PitcherGameStats stats = null;
		if (game.getPitcherGameStatsMap() != null) {
			stats = game.getPitcherGameStatsMap().get(decodedPlayerName);
		}
		if (stats == null) {
			stats = PitcherGameStats.builder()
					.playerName(decodedPlayerName)
					.strikeouts(0)
					.walks(0)
					.hitsAllowed(0)
					.homersAllowed(0)
					.pitches(0)
					.outsRecorded(0)
					.earnedRunsAllowed(0)
					.build();
		}
		return ApiResponse.success(stats);
	}

	/**
	 * GameDto를 GamePlayView로 변환하는 헬퍼 메서드
	 */
	private GamePlayView buildGamePlayView(GameDto game) {
		// 보기 응답에서도 정렬 반영
		sortStatsMapsForResponse(game);
		BatterGameStats batterStats = null;
		if (game.getCurrentBatter() != null) {
			String bname = game.getCurrentBatter().getName();
			batterStats = (game.getBatterGameStatsMap() != null) ? game.getBatterGameStatsMap().get(bname) : null;
			if (batterStats == null) {
				batterStats = BatterGameStats.builder()
						.playerName(bname)
						.plateAppearances(0)
						.atBats(0)
						.hits(0)
						.homeRuns(0)
						.walks(0)
						.strikeouts(0)
						.rbis(0)
						.build();
			}
		}

		PitcherGameStats pitcherStats = null;
		if (game.getCurrentPitcher() != null) {
			String pname = game.getCurrentPitcher().getName();
			pitcherStats = (game.getPitcherGameStatsMap() != null) ? game.getPitcherGameStatsMap().get(pname) : null;
			if (pitcherStats == null) {
				pitcherStats = PitcherGameStats.builder()
						.playerName(pname)
						.strikeouts(0)
						.walks(0)
						.hitsAllowed(0)
						.homersAllowed(0)
						.pitches(0)
						.outsRecorded(0)
						.earnedRunsAllowed(0)
						.build();
			}
		}

		return GamePlayView.builder()
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
				.homeHit(game.getHomeHit())
				.awayHit(game.getAwayHit())
				.homeWalks(game.getHomeWalks())
				.awayWalks(game.getAwayWalks())
				.currentBatter(game.getCurrentBatter())
				.currentPitcher(game.getCurrentPitcher())
				// 홈 슬록(0)을 제외하고 1루, 2루, 3루만 응답으로 제공
				.bases(extractBasesWithoutHome(game.getBases()))
				.eventLog(game.getEventLog())
				.batterGameStats(batterStats)
				.pitcherGameStats(pitcherStats)
				.build();
	}

	// 이닝별 득점과 합계를 집계한 스코어보드 뷰 구성
	private com.baseball.game.dto.ScoreboardDto buildScoreboardView(GameDto game) {
		int max = Math.max(1, game.getMaxInning());
		java.util.List<Integer> home = new java.util.ArrayList<>(java.util.Collections.nCopies(max, 0));
		java.util.List<Integer> away = new java.util.ArrayList<>(java.util.Collections.nCopies(max, 0));

		// 이벤트 로그에서 PA_END 단위로 이닝별 득점을 집계
		if (game.getEventLog() != null) {
			for (com.baseball.game.dto.PlayEvent ev : game.getEventLog()) {
				if (ev == null)
					continue;
				if (!"PA_END".equals(ev.getType()))
					continue;
				int inningIdx = Math.max(1, ev.getInning()) - 1; // 0-based
				if (inningIdx >= max)
					continue;
				int hs = Math.max(0, ev.getHomeScore());
				int as = Math.max(0, ev.getAwayScore());
				// 해당 이벤트 시점의 총점으로부터 해당 이닝 점수를 추론하기는 어렵기 때문에,
				// 간단히 이전 이벤트 대비 증가분을 계산. 단, 이닝 경계 변경 시에도 누적 증가만 반영됨
				// 안정적으로 계산하려면 runsBefore/After를 이벤트에 담아야 하지만, 현재는 스냅샷만 있음.
				// 따라서 스냅샷 기반 차분: 직전 같은 팀 점수 대비 증가를 이 이닝에 더함.
			}
		}

		// 차분 계산을 위해 시계열 순회
		int prevHome = 0, prevAway = 0;
		if (game.getEventLog() != null) {
			for (com.baseball.game.dto.PlayEvent ev : game.getEventLog()) {
				if (ev == null)
					continue;
				if (!"PA_END".equals(ev.getType()))
					continue;
				int inningIdx = Math.max(1, ev.getInning()) - 1;
				if (inningIdx >= max)
					continue;
				int hs = Math.max(0, ev.getHomeScore());
				int as = Math.max(0, ev.getAwayScore());
				int dh = Math.max(0, hs - prevHome);
				int da = Math.max(0, as - prevAway);
				if (dh > 0) {
					home.set(inningIdx, home.get(inningIdx) + dh);
				}
				if (da > 0) {
					away.set(inningIdx, away.get(inningIdx) + da);
				}
				prevHome = hs;
				prevAway = as;
			}
		}

		return com.baseball.game.dto.ScoreboardDto.builder()
				.gameId(game.getGameId())
				.homeTeam(game.getHomeTeam())
				.awayTeam(game.getAwayTeam())
				.currentInning(game.getInning())
				.maxInning(game.getMaxInning())
				.isTop(game.isTop())
				.homeByInning(home)
				.awayByInning(away)
				.homeScore(game.getHomeScore())
				.awayScore(game.getAwayScore())
				.homeHit(game.getHomeHit())
				.awayHit(game.getAwayHit())
				.homeWalks(game.getHomeWalks())
				.awayWalks(game.getAwayWalks())
				.build();
	}

	// bases[0](홈)을 제외하고 1루~3루만 3칸 배열로 변환
	private com.baseball.game.dto.Batter[] extractBasesWithoutHome(com.baseball.game.dto.Batter[] bases) {
		if (bases == null)
			return null;
		com.baseball.game.dto.Batter[] threeBases = new com.baseball.game.dto.Batter[3];
		threeBases[0] = bases.length > 1 ? bases[1] : null; // 1루
		threeBases[1] = bases.length > 2 ? bases[2] : null; // 2루
		threeBases[2] = bases.length > 3 ? bases[3] : null; // 3루
		return threeBases;
	}

	// batterGameStatsMap, pitcherGameStatsMap을 팀과 타순 순서로 정렬
	private void sortStatsMapsForResponse(GameDto game) {
		try {
			if (game == null)
				return;
			// 타자 정렬: 홈 타순 → 원정 타순 순서로 키 배열 구성
			java.util.List<String> homeOrder = new java.util.ArrayList<>();
			if (game.getHomeBattingOrder() != null) {
				for (com.baseball.game.dto.Batter b : game.getHomeBattingOrder()) {
					if (b != null && b.getName() != null)
						homeOrder.add(b.getName());
				}
			}
			java.util.List<String> awayOrder = new java.util.ArrayList<>();
			if (game.getAwayBattingOrder() != null) {
				for (com.baseball.game.dto.Batter b : game.getAwayBattingOrder()) {
					if (b != null && b.getName() != null)
						awayOrder.add(b.getName());
				}
			}
			java.util.Map<String, Integer> orderIndex = new java.util.HashMap<>();
			int idx = 0;
			for (String name : homeOrder)
				orderIndex.put(name, idx++);
			for (String name : awayOrder)
				orderIndex.putIfAbsent(name, idx++);

			if (game.getBatterGameStatsMap() != null && !game.getBatterGameStatsMap().isEmpty()) {
				java.util.Map<String, com.baseball.game.dto.BatterGameStats> sorted = new LinkedHashMap<>();
				game.getBatterGameStatsMap().entrySet().stream()
						.sorted(Comparator.comparingInt(e -> orderIndex.getOrDefault(e.getKey(), Integer.MAX_VALUE)))
						.forEach(e -> sorted.put(e.getKey(), e.getValue()));
				game.setBatterGameStatsMap(sorted);
			}

			// 투수 정렬: 홈 선발 → 원정 선발 우선, 그 외는 이름순
			java.util.Set<String> pitcherPriority = new java.util.LinkedHashSet<>();
			if (game.getHomeStartingPitcher() != null && game.getHomeStartingPitcher().getName() != null) {
				pitcherPriority.add(game.getHomeStartingPitcher().getName());
			}
			if (game.getAwayStartingPitcher() != null && game.getAwayStartingPitcher().getName() != null) {
				pitcherPriority.add(game.getAwayStartingPitcher().getName());
			}
			if (game.getPitcherGameStatsMap() != null && !game.getPitcherGameStatsMap().isEmpty()) {
				java.util.Map<String, com.baseball.game.dto.PitcherGameStats> sortedP = new LinkedHashMap<>();
				game.getPitcherGameStatsMap().entrySet().stream()
						.sorted((a, b) -> {
							String ka = a.getKey(), kb = b.getKey();
							boolean pa = pitcherPriority.contains(ka);
							boolean pb = pitcherPriority.contains(kb);
							if (pa != pb)
								return pa ? -1 : 1;
							return ka.compareTo(kb);
						})
						.forEach(e -> sortedP.put(e.getKey(), e.getValue()));
				game.setPitcherGameStatsMap(sortedP);
			}
		} catch (Exception ignored) {
		}
	}

	private String ensureUtf8PlayerName(String name) {
		if (name == null)
			return null;
		if (containsHangul(name))
			return name; // 이미 정상
		if (looksLikeLatin1(name)) {
			try {
				return new String(name.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
			} catch (Exception ignored) {
			}
		}
		return name;
	}

	private boolean containsHangul(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if ((c >= 0xAC00 && c <= 0xD7AF) || (c >= 0x1100 && c <= 0x11FF)) {
				return true;
			}
		}
		return false;
	}

	private boolean looksLikeLatin1(String s) {
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0x80 && c <= 0xFF) {
				return true;
			}
		}
		return false;
	}
}