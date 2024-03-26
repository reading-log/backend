package com.api.readinglog.domain.member.service;

import static org.junit.jupiter.api.Assertions.*;

import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.jwt.JwtTokenProvider;
import com.api.readinglog.domain.member.controller.dto.request.JoinRequest;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import com.api.readinglog.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;

/**
@SpringBootTest
public class MemberTokenReissueTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    private final String memberEmail = "test@test.com";
    private final String memberPassword = "12345asd!";

    @BeforeEach
    void joinMember() {
        JoinRequest joinRequest = createJoinRequest();
        memberService.join(joinRequest);
    }

    @AfterEach
    void deleteMember() {
        memberRepository.delete(memberService.getMemberByEmailAndRole(memberEmail, MemberRole.MEMBER_NORMAL));
    }

    @Test
    void reissueTokenTest() {
        Member member = memberService.getMemberByEmailAndRole(memberEmail, MemberRole.MEMBER_NORMAL);

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                member.getEmail(), memberPassword);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 액세스 토큰과 리프레시 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        // 리프레시 토큰으로 액세스 토큰 재발급
        JwtToken newJwtToken = memberService.reissueToken(jwtToken.getRefreshToken());

        String newAccessToken = newJwtToken.getAccessToken();

        // 재발급된 액세스 토큰의 유효성 검사
        boolean isValid = jwtTokenProvider.validateToken(newAccessToken);
        assertTrue(isValid);

        // 재발급된 액세스 토큰으로부터 사용자 정보 추출 및 검증
        Authentication reissuedAuthentication = jwtTokenProvider.getAuthenticationFromAccessToken(newAccessToken);
        assertEquals(member.getEmail(), reissuedAuthentication.getName());
    }

    private JoinRequest createJoinRequest() {
        JoinRequest joinRequest = new JoinRequest();
        joinRequest.setEmail(memberEmail);
        joinRequest.setNickname("nickname");
        joinRequest.setPassword(memberPassword);
        joinRequest.setPasswordConfirm(memberPassword);
        return joinRequest;
    }

}
 */