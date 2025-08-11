package com.baseball.game.constant;

public final class GameConstants {

    private GameConstants() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    // 게임 설정
    public static final int MIN_INNINGS = 3;
    public static final int MAX_INNINGS = 9;
    public static final int MAX_OUTS = 3;
    public static final int MAX_STRIKES = 3;
    public static final int MAX_BALLS = 4;
    public static final int PLAYERS_PER_TEAM = 9;

    // 베이스 관련
    public static final int HOME_BASE = 0;
    public static final int FIRST_BASE = 1;
    public static final int SECOND_BASE = 2;
    public static final int THIRD_BASE = 3;

    // 투구 타입
    public static final String PITCH_TYPE_STRIKE = "strike";
    public static final String PITCH_TYPE_BALL = "ball";

    // 게임 상태
    public static final String GAME_STATE_TOP = "top";
    public static final String GAME_STATE_BOTTOM = "bottom";

    // 에러 메시지
    public static final String ERROR_GAME_NOT_FOUND = "게임을 찾을 수 없습니다.";
    public static final String ERROR_INVALID_GAME_STATE = "잘못된 게임 상태입니다.";
    public static final String ERROR_VALIDATION_FAILED = "입력값 검증에 실패했습니다.";
}