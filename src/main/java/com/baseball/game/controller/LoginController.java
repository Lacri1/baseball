package com.baseball.game.controller;

import com.baseball.game.dto.MemberDto;
import com.baseball.game.mapper.MemberMapper;
import com.baseball.game.exception.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Setter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*")
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Setter(onMethod_ = @Autowired)
    private MemberMapper memberMapper;

    // 로그인
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody MemberDto memberDto) {
        logger.info("로그인 요청: id={}", memberDto.getId());

        // 입력값 검증
        if (memberDto.getId() == null || memberDto.getId().trim().isEmpty()) {
            throw new ValidationException("아이디는 필수입니다.");
        }
        if (memberDto.getPw() == null || memberDto.getPw().trim().isEmpty()) {
            throw new ValidationException("비밀번호는 필수입니다.");
        }

        Map<String, Object> response = new HashMap<>();
        try {
            boolean success = memberMapper.login(memberDto.getId(), memberDto.getPw());
            if (success) {
                response.put("success", true);
                response.put("message", "로그인 성공");
                logger.info("로그인 성공: id={}", memberDto.getId());
            } else {
                response.put("success", false);
                response.put("message", "아이디 또는 비밀번호가 올바르지 않습니다.");
                logger.warn("로그인 실패: id={}", memberDto.getId());
            }
        } catch (Exception e) {
            logger.error("로그인 처리 중 오류 발생: id={}", memberDto.getId(), e);
            response.put("success", false);
            response.put("message", "로그인 처리 중 오류가 발생했습니다.");
        }
        return response;
    }

    // 회원가입
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody MemberDto memberDto) {
        logger.info("회원가입 요청: id={}", memberDto.getId());

        // 입력값 검증
        if (memberDto.getId() == null || memberDto.getId().trim().isEmpty()) {
            throw new ValidationException("아이디는 필수입니다.");
        }
        if (memberDto.getPw() == null || memberDto.getPw().trim().isEmpty()) {
            throw new ValidationException("비밀번호는 필수입니다.");
        }
        if (memberDto.getTeam() == null || memberDto.getTeam().trim().isEmpty()) {
            throw new ValidationException("팀명은 필수입니다.");
        }

        Map<String, Object> response = new HashMap<>();
        try {
            if (memberMapper.checkId(memberDto.getId())) {
                response.put("success", false);
                response.put("message", "이미 존재하는 아이디입니다.");
                logger.warn("회원가입 실패 - 중복 아이디: id={}", memberDto.getId());
                return response;
            }
            memberMapper.register(memberDto.getId(), memberDto.getPw(), memberDto.getTeam());
            response.put("success", true);
            response.put("message", "회원가입 성공");
            logger.info("회원가입 성공: id={}", memberDto.getId());
        } catch (Exception e) {
            logger.error("회원가입 처리 중 오류 발생: id={}", memberDto.getId(), e);
            response.put("success", false);
            response.put("message", "회원가입 처리 중 오류가 발생했습니다.");
        }
        return response;
    }
}
