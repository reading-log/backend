package com.api.readinglog.domain.member.controller;

import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.common.security.util.CookieUtils;
import com.api.readinglog.domain.member.controller.dto.request.DeleteRequest;
import com.api.readinglog.domain.member.controller.dto.request.JoinRequest;
import com.api.readinglog.domain.member.controller.dto.request.LoginRequest;
import com.api.readinglog.domain.member.controller.dto.request.UpdateProfileRequest;
import com.api.readinglog.domain.member.controller.dto.response.MemberDetailsResponse;
import com.api.readinglog.domain.member.service.MemberService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
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
    public Response<Void> join(@ModelAttribute @Valid JoinRequest request) {
        memberService.join(request);
        return Response.success(HttpStatus.CREATED, "회원 가입 완료");
    }

    @PostMapping("/login")
    public Response<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtToken jwtToken = memberService.login(request);
        CookieUtils.addCookie(response, "accessToken", jwtToken.getAccessToken(), 24 * 60 * 60);
        CookieUtils.addCookie(response, "refreshToken", jwtToken.getRefreshToken(), 24 * 60 * 60 * 7);
        return Response.success(HttpStatus.OK, "로그인 성공!");
    }

    @GetMapping("/me")
    public Response<MemberDetailsResponse> findMember(@AuthenticationPrincipal CustomUserDetail user) {
        MemberDetailsResponse member = memberService.getMemberDetails(user.getId());
        return Response.success(HttpStatus.OK, "회원 조회 성공!", member);
    }

    @PatchMapping("/me")
    public Response<Void> updateProfile(@AuthenticationPrincipal CustomUserDetail user,
                                        @ModelAttribute @Valid UpdateProfileRequest request) {
        memberService.updateProfile(user.getId(), request);
        return Response.success(HttpStatus.OK, "회원 수정 성공!");
    }

    @DeleteMapping("/me")
    public Response<Void> deleteMember(@AuthenticationPrincipal CustomUserDetail user,
                                       @RequestBody DeleteRequest request) {
        memberService.deleteMember(user.getId(), request);
        return Response.success(HttpStatus.OK, "일반 회원 탈퇴 성공!");
    }

    @DeleteMapping("/social/me")
    public Response<Void> deleteSocialMember(@AuthenticationPrincipal CustomUserDetail user) {
        memberService.deleteSocialMember(user.getId());
        return Response.success(HttpStatus.OK, "소셜 회원 탈퇴 성공!");
    }
}
