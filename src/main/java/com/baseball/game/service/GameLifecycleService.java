package com.baseball.game.service;

import com.baseball.game.dto.GameDto;

public interface GameLifecycleService {

    /**
     * 새로운 게임을 생성합니다.
     * 
     * @param homeTeam      홈팀 이름
     * @param awayTeam      원정팀 이름
     * @param maxInning     최대 이닝 수
     * @param isUserOffense 사용자가 공격하는지 여부
     * @return 생성된 게임 DTO
     */
    GameDto createGame(String homeTeam, String awayTeam, int maxInning, boolean isUserOffense);

    /**
     * 게임 ID로 게임을 조회합니다.
     * 
     * @param gameId 게임 ID
     * @return 게임 DTO
     */
    GameDto getGame(String gameId);

    /**
     * 게임을 리셋합니다.
     * 
     * @param gameId 게임 ID
     */
    void resetGame(String gameId);

    /**
     * 게임이 존재하는지 확인합니다.
     * 
     * @param gameId 게임 ID
     * @return 존재 여부
     */
    boolean existsGame(String gameId);

    /**
     * 현재 메모리 상의 게임 상태를 영속 저장소(예: Redis)에 저장합니다.
     * 영속 저장소가 주입되지 않은 환경에서는 no-op.
     */
    void saveGame(String gameId);

    java.util.List<GameDto> getAllGames();
}