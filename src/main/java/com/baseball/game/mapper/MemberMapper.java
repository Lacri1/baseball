package com.baseball.game.mapper;

import com.baseball.game.dto.MemberDto;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberMapper {

	public boolean checkId(String Id);

	public boolean checkNickname(String nickname);

	public boolean checkNicknameForUpdate(String nickname, String id);

	public void register(String Id, String Pw, String email, String nickname);

	public MemberDto member(String Id);

	// 게임/승/패 카운트 업데이트
	void incrementGame(@Param("Id") String id);

	void incrementWin(@Param("Id") String id);

	void incrementLose(@Param("Id") String id);

	void incrementDraw(@Param("Id") String id);

	void updateMember(MemberDto memberDto);

	void deleteMember(String id);
}
