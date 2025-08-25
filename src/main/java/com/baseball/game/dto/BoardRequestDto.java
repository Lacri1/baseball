package com.baseball.game.dto;
import lombok.Data;

@Data
public class BoardRequestDto {
	private String title;
	private String writer;
	private String text;
	private String category;
}
