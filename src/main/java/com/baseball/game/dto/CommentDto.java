package com.baseball.game.dto;

import lombok.Data;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CommentDto {
	private Integer commentId;
	private int boardNo;
	private String writer;
	private String text;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createdAt;
}
