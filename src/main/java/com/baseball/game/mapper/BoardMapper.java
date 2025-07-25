package com.baseball.game.mapper;

import com.baseball.game.dto.BoardDto;
import java.util.ArrayList;

public interface BoardMapper {
	public ArrayList<BoardDto> getList();

	public ArrayList<BoardDto> getListCategory(String category);

	public void write(String title, String text, String writer, String categhory);

	public void modify(int no, String text);

	public void delete(int no);

	public BoardDto getBoard(int no);

	ArrayList<BoardDto> getPagedList(int offset, int size);
	
	ArrayList<BoardDto> getPagedListCate(int offset, int size,String category);

	int getTotalCount();
}
