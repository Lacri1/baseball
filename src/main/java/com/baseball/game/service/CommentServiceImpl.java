package com.baseball.game.service;
import com.baseball.game.dto.CommentDto;
import com.baseball.game.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import lombok.Setter;
public class CommentServiceImpl implements CommentService{
	@Setter(onMethod_=@Autowired)
	public CommentMapper mapper;
	
	@Override
	public ArrayList<CommentDto> getComment(int boardNo){
		ArrayList<CommentDto> m=mapper.getComment(boardNo);
		return m;
	}
	@Override
	public void comment(CommentDto d) {
		mapper.comment(d);
	}
	@Override
	public void delcom(int boardNo,int commentId) {
		mapper.delcom(boardNo, commentId);
	}
	@Override
	public void modify(int boardNo,int commentId,String text) {
		mapper.modify(boardNo, commentId, text);
	}
}
