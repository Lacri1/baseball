package com.baseball.game.mapper;

import com.baseball.game.dto.CommentDto;
import java.util.ArrayList;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper {
	public ArrayList<CommentDto> getComment(int boardNo);

	public void comment(CommentDto d);

	public void delcom(int boardNo, int commentId);

	public void modify(int boardNo, int commentId, String text);
}
