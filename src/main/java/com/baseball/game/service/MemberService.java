package com.baseball.game.service;

import com.baseball.game.dto.MemberDto;
import java.util.Map;
public interface MemberService {

    boolean checkId(String id);

    boolean checkNickname(String nickname);

    boolean checkNicknameForUpdate(String nickname, String id);

    void register(MemberDto memberDto);

    MemberDto getMember(String id);

    Map<String, Object> loginProcess(MemberDto memberDto);

    Map<String, Object> registerProcess(MemberDto memberDto);

    void updateMember(MemberDto memberDto);

    void deleteMember(String id);
}