package com.baseball.game.service;

import com.baseball.game.dto.CustomLineupRequest;
import com.baseball.game.dto.TeamLineup;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.mapper.TeamLineupMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TeamLineupServiceImplTest {

    @InjectMocks
    private TeamLineupServiceImpl service;

    @Mock
    private TeamLineupMapper teamLineupMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getDefaultLineup: DB에서 기본 라인업을 올바르게 조회한다")
    void getDefaultLineup_shouldFetchFromDb() {
        // Given
        String teamName = "SSG 랜더스";
        List<TeamLineup> mockLineup = new ArrayList<>();
        mockLineup.add(new TeamLineup()); // Dummy data
        given(teamLineupMapper.findDefaultLineupByTeam(teamName)).willReturn(mockLineup);

        // When
        List<TeamLineup> result = service.getDefaultLineup(teamName);

        // Then
        assertThat(result).isSameAs(mockLineup);
        verify(teamLineupMapper).findDefaultLineupByTeam(teamName);
    }

    @Test
    @DisplayName("saveCustomLineup: 정상적인 요청 시 라인업을 저장한다")
    void saveCustomLineup_shouldSaveValidLineup() {
        // Given
        String userId = "user1";
        String teamName = "Giants";
        List<CustomLineupRequest.LineupPosition> lineupPositions = new ArrayList<>();
        for (int i = 0; i < 10; i++) { // 9 batters, 1 pitcher
            CustomLineupRequest.LineupPosition pos = new CustomLineupRequest.LineupPosition();
            pos.setPlayerName("Player " + i);
            pos.setPlayerId(i);
            pos.setPosition(i < 9 ? i + 1 : null);
            lineupPositions.add(pos);
        }
        CustomLineupRequest req = new CustomLineupRequest();
        req.setUserId(userId);
        req.setTeamName(teamName);
        req.setLineup(lineupPositions);

        // Mock mapper methods
        doNothing().when(teamLineupMapper).deleteCustomLineupByUserAndTeam(userId, teamName);
        doNothing().when(teamLineupMapper).insertCustomLineup(any(TeamLineup.class));

        // When
        service.saveCustomLineup(req);

        // Then
        verify(teamLineupMapper).deleteCustomLineupByUserAndTeam(userId, teamName);
        verify(teamLineupMapper, times(10)).insertCustomLineup(any(TeamLineup.class));
    }

    @Test
    @DisplayName("getCustomLineup: 저장된 커스텀 라인업을 조회한다")
    void getCustomLineup_shouldReturnSavedLineup() {
        // Given
        String userId = "user1";
        String teamName = "Giants";
        List<TeamLineup> mockCustomLineup = new ArrayList<>();
        mockCustomLineup.add(new TeamLineup());
        given(teamLineupMapper.findCustomLineupByUserAndTeam(userId, teamName)).willReturn(mockCustomLineup);

        // When
        List<TeamLineup> result = service.getCustomLineup(userId, teamName);

        // Then
        assertThat(result).isSameAs(mockCustomLineup);
    }


    @Test
    @DisplayName("saveCustomLineup: 라인업이 10명이 아닐 경우 ValidationException 발생")
    void saveCustomLineup_shouldThrowException_whenLineupSizeIsNot10() {
        // Given
        String userId = "user2";
        String teamName = "Giants";
        List<CustomLineupRequest.LineupPosition> lineup = new ArrayList<>();
        // 9명만 추가
        for (int i = 0; i < 9; i++) {
            lineup.add(new CustomLineupRequest.LineupPosition());
        }
        CustomLineupRequest req = new CustomLineupRequest();
        req.setUserId(userId);
        req.setTeamName(teamName);
        req.setLineup(lineup);

        // When & Then
        assertThatThrownBy(() -> service.saveCustomLineup(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("라인업은 정확히 10명");
    }

    @Test
    @DisplayName("getAvailablePlayers: 팀별 사용 가능한 선수 목록을 반환한다")
    void getAvailablePlayers_shouldReturnPlayerList() {
        // Given
        String teamName = "롯데 자이언츠";
        List<String> mockPlayers = Arrays.asList("황성빈", "윤동희");
        given(teamLineupMapper.findAvailablePlayersByTeam(teamName)).willReturn(mockPlayers);

        // When
        List<String> result = service.getAvailablePlayers(teamName);

        // Then
        assertThat(result).isEqualTo(mockPlayers);
        verify(teamLineupMapper).findAvailablePlayersByTeam(teamName);
    }
}
