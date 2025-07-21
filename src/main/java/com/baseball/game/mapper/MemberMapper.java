package com.baseball.game.mapper;

public interface MemberMapper {
	public boolean login(String Id,String Pw);
	public boolean checkId(String Id);
	public void register(String Id,String Pw,String Team);
	public void member(String Id);
}
