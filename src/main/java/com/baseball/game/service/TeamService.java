package com.baseball.game.service;

import java.util.List;

import com.baseball.game.dto.Player;
import com.baseball.game.dto.TeamRoster;

public interface TeamService {
	 public List<Player> getPlayersByTeam(String teamId);

	 /**
	  * 팀별 로스터(타자+투수)를 함께 반환
	  */
	 public TeamRoster getRosterByTeam(String teamId);
}