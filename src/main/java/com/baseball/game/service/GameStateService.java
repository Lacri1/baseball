package com.baseball.game.service;

import com.baseball.game.dto.GameDto;

public interface GameStateService {

    /**
     * 다음 이닝으로 진행합니다.
     * 
     * @param gameId 게임 ID
     * @return 업데이트된 게임 DTO
     */
    GameDto nextInning(String gameId);

    /**
     * 게임을 종료합니다.
     * 
     * @param gameId 게임 ID
     * @return 업데이트된 게임 DTO
     */
    GameDto endGame(String gameId);

    /**
     * 베이스 러너를 진루시킵니다.
     * 
     * @param gameId         게임 ID
     * @param basesToAdvance 진루할 베이스 수
     */
    void advanceRunners(String gameId, Integer basesToAdvance);

    /**
     * 카운트를 확인하고 필요한 경우 타순을 진행시킵니다.
     * 
     * @param gameId 게임 ID
     */
    void checkCount(String gameId);

    /**
     * 타순을 다음 타자로 진행시킵니다.
     * 
     * @param gameId 게임 ID
     */
    void advanceBattingOrder(String gameId);

    /**
     * 게임 종료 조건을 확인합니다.
     * 
     * @param gameId 게임 ID
     */
    void checkGameOver(String gameId);

    /**
     * 점수를 처리합니다.
     * 
     * @param gameId 게임 ID
     * @param score  획득한 점수
     */
    void handleScore(String gameId, int score);
}