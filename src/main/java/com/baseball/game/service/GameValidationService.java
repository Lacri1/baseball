package com.baseball.game.service;

import com.baseball.game.dto.GameDto;

public interface GameValidationService {

    /**
     * 게임이 진행 가능한 상태인지 확인합니다.
     * 
     * @param gameId 게임 ID
     * @return 진행 가능 여부
     */
    boolean isGamePlayable(String gameId);

    /**
     * 현재 게임 상태가 유효한지 확인합니다.
     * 
     * @param game 게임 DTO
     * @return 유효성 여부
     */
    boolean isValidGameState(GameDto game);

    /**
     * 타격이 가능한 상태인지 확인합니다.
     * 
     * @param gameId 게임 ID
     * @return 타격 가능 여부
     */
    boolean canBatterSwing(String gameId);

    /**
     * 투구가 가능한 상태인지 확인합니다.
     * 
     * @param gameId 게임 ID
     * @return 투구 가능 여부
     */
    boolean canPitcherThrow(String gameId);

    /**
     * 이닝 전환이 가능한지 확인합니다.
     * 
     * @param gameId 게임 ID
     * @return 이닝 전환 가능 여부
     */
    boolean canChangeInning(String gameId);
}