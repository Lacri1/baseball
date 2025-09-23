package com.baseball.game.dto;

import lombok.Data;

@Data
public class MemberDto {
	private String id;
	private String pw;
	private String email;
	private String nickname;
	private int game;
	private int win;
	private int lose;
	private int draw;
}
