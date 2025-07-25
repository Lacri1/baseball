package com.baseball.game.service;
import com.baseball.game.dto.BoardDto;
import com.baseball.game.dto.BoardPageResponse;
import java.util.ArrayList;

public interface BoardService {
	public ArrayList<BoardDto> getList();
	public ArrayList<BoardDto> getListCategory(String category);
	public void write(String title,String writer, String text, String category);
	public void modify(int no,String text);
	public void delete(int no);
	public BoardDto getBoard(int no);
	public BoardPageResponse getPagedList(int page, int size);
	public BoardPageResponse getPagedListCategory(int page, int size,String category);
}
