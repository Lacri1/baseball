package com.baseball.game.util;

import com.baseball.game.dto.GameCreateRequest;
import com.baseball.game.dto.TeamLineupSetRequest;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.constant.GameConstants;

import java.util.List;

public class ValidationUtil {

    public static void validateGameId(String gameId) {
        if (gameId == null || gameId.trim().isEmpty()) {
            throw new ValidationException("게임 ID는 필수입니다.");
        }

        if (!gameId.matches("^[a-zA-Z0-9-]+$")) {
            throw new ValidationException("게임 ID는 영문자, 숫자, 하이픈만 허용됩니다.");
        }
    }

    public static void validateTiming(Double timing) {
        if (timing == null) {
            throw new ValidationException("타이밍 값은 필수입니다.");
        }

        if (timing < 0.0 || timing > 1.0) {
            throw new ValidationException("타이밍 값은 0.0에서 1.0 사이여야 합니다.");
        }
    }

    public static void validateSwing(Boolean swing) {
        if (swing == null) {
            throw new ValidationException("스윙 여부는 필수입니다.");
        }
    }

    public static void validatePitchType(String pitchType) {
        if (pitchType == null || pitchType.trim().isEmpty()) {
            throw new ValidationException("투구 타입은 필수입니다.");
        }

        if (!pitchType.equals("strike") && !pitchType.equals("ball")) {
            throw new ValidationException("투구 타입은 'strike' 또는 'ball'이어야 합니다.");
        }
    }

    public static void validateBases(Integer bases) {
        if (bases == null) {
            throw new ValidationException("베이스 수는 필수입니다.");
        }

        if (bases < 1 || bases > 4) {
            throw new ValidationException("베이스 수는 1에서 4 사이여야 합니다.");
        }
    }

    public static void validateTeamName(String teamName) {
        if (teamName == null || teamName.trim().isEmpty()) {
            throw new ValidationException("팀명은 필수입니다.");
        }

        if (teamName.length() > 50) {
            throw new ValidationException("팀명은 50자를 초과할 수 없습니다.");
        }
    }

    public static void validateDifferentTeams(String homeTeam, String awayTeam) {
        if (homeTeam == null || awayTeam == null) {
            throw new ValidationException("홈팀과 원정팀 모두 필수입니다.");
        }

        if (homeTeam.trim().equalsIgnoreCase(awayTeam.trim())) {
            throw new ValidationException("홈팀과 원정팀은 서로 다른 팀이어야 합니다.");
        }
    }

    public static void validateMaxInning(Integer maxInning) {
        if (maxInning == null) {
            throw new ValidationException("최대 이닝 수는 필수입니다.");
        }

        if (maxInning < GameConstants.MIN_INNINGS || maxInning > GameConstants.MAX_INNINGS) {
            throw new ValidationException(
                    "최대 이닝 수는 " + GameConstants.MIN_INNINGS + "에서 " + GameConstants.MAX_INNINGS + " 사이여야 합니다.");
        }
    }

    /**
     * 타순의 유효성을 검사합니다.
     * 
     * @param battingOrder 타순 리스트
     */
    public static void validateBattingOrder(List<String> battingOrder) {
        if (battingOrder == null || battingOrder.isEmpty()) {
            throw new ValidationException("타순은 필수입니다.");
        }
        if (battingOrder.size() != GameConstants.PLAYERS_PER_TEAM) {
            throw new ValidationException("타순은 " + GameConstants.PLAYERS_PER_TEAM + "명이어야 합니다.");
        }
    }

    /**
     * 투수명의 유효성을 검사합니다.
     * 
     * @param pitcherName 투수명
     */
    public static void validatePitcherName(String pitcherName) {
        if (pitcherName == null || pitcherName.trim().isEmpty()) {
            throw new ValidationException("투수명은 필수입니다.");
        }
    }

    /**
     * 게임 생성 요청 DTO의 유효성을 검사합니다.
     * 
     * @param request 게임 생성 요청 DTO
     */
    public static void validateGameCreateRequest(GameCreateRequest request) {
        if (request == null) {
            throw new ValidationException("요청 본문이 비어있습니다.");
        }
        validateTeamName(request.getHomeTeam());
        validateTeamName(request.getAwayTeam());
        validateDifferentTeams(request.getHomeTeam(), request.getAwayTeam());
        validateMaxInning(request.getMaxInning());
    }

    /**
     * 팀 라인업 설정 요청 DTO의 유효성을 검사합니다.
     * 
     * @param request 팀 라인업 설정 요청 DTO
     */
    public static void validateTeamLineupSetRequest(TeamLineupSetRequest request) {
        if (request == null) {
            throw new ValidationException("요청 본문이 비어있습니다.");
        }
        validateTeamName(request.getTeamName());
        validateBattingOrder(request.getBattingOrder());
        validatePitcherName(request.getStartingPitcher());
    }
}
