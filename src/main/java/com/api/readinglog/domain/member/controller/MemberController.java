package com.api.readinglog.domain.member.controller;

import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.response.Response;
import com.api.readinglog.domain.member.controller.dto.JoinRequest;
import com.api.readinglog.domain.member.controller.dto.LoginRequest;
import com.api.readinglog.domain.member.controller.dto.LoginResponse;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/join")
    public Response<Void> join(@ModelAttribute JoinRequest request) {
        memberService.join(request);
        return Response.success(HttpStatus.CREATED, "회원 가입 완료");
    }

    @PostMapping("/login")
    public Response<LoginResponse> login(@RequestBody LoginRequest request) {
        JwtToken jwtToken = memberService.login(request);
        return Response.success(HttpStatus.OK, "로그인 성공!", LoginResponse.of(jwtToken));
    }

    @GetMapping
    public Response<Member> my(Authentication authentication) {
        String email = authentication.getName();
        Member member = memberService.getMemberByEmail(email);
        return Response.success(HttpStatus.OK, "회원 조회 성공!", member);
    }

}
