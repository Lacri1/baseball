package com.baseball.game.service;

import com.baseball.game.dto.MemberDto;
import com.baseball.game.exception.ValidationException;
import com.baseball.game.mapper.MemberMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {

    @Mock
    private MemberMapper memberMapper;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("loginProcess - 성공")
    void loginProcess_success() {
        MemberDto req = new MemberDto();
        req.setId("u1");
        req.setPw("p1");
        when(memberMapper.login(anyString(), anyString())).thenReturn(true);

        Map<String, Object> resp = memberService.loginProcess(req);
        assertThat(resp.get("success")).isEqualTo(true);
    }

    @Test
    @DisplayName("loginProcess - 검증 실패(아이디 없음)")
    void loginProcess_validation_id() {
        MemberDto req = new MemberDto();
        req.setPw("p1");
        assertThatThrownBy(() -> memberService.loginProcess(req))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("아이디는 필수입니다.");
    }

    @Test
    @DisplayName("registerProcess - 중복 아이디")
    void registerProcess_duplicate() {
        MemberDto req = new MemberDto();
        req.setId("u1");
        req.setPw("p1");
        req.setEmail("T");
        when(memberMapper.checkId("u1")).thenReturn(true);

        Map<String, Object> resp = memberService.registerProcess(req);
        assertThat(resp.get("success")).isEqualTo(false);
    }

    @Test
    @DisplayName("registerProcess - 성공")
    void registerProcess_success() {
        MemberDto req = new MemberDto();
        req.setId("u2");
        req.setPw("p2");
        req.setEmail("T");
        when(memberMapper.checkId("u2")).thenReturn(false);

        Map<String, Object> resp = memberService.registerProcess(req);
        assertThat(resp.get("success")).isEqualTo(true);
    }
}
