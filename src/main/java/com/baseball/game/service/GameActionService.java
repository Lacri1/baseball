package com.baseball.game.service;

public interface GameActionService {

    /**
     * 타자가 스윙합니다.
     * 
     * @param gameId 게임 ID
     * @param swing  스윙 여부
     * @param timing 타이밍 (0.0 ~ 1.0)
     * @return 결과 메시지
     */
    String batterSwing(String gameId, Boolean swing, double timing);

    /**
     * 투수가 투구합니다.
     * 
     * @param gameId    게임 ID
     * @param pitchType 투구 타입
     * @return 결과 메시지
     */
    String pitcherThrow(String gameId, String pitchType);

    /**
     * 컴퓨터의 턴을 진행합니다.
     * 
     * @param gameId 게임 ID
     * @return 결과 메시지
     */
    String playComputerTurn(String gameId);
}