package com.api.readinglog.domain.member.controller;

import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.common.security.util.CookieUtils;
import com.api.readinglog.domain.email.dto.AuthCodeVerificationRequest;
import com.api.readinglog.domain.email.dto.EmailRequest;
import com.api.readinglog.domain.email.service.EmailService;
import com.api.readinglog.domain.member.controller.dto.request.DeleteRequest;
import com.api.readinglog.domain.member.controller.dto.request.JoinNicknameRequest;
import com.api.readinglog.domain.member.controller.dto.request.JoinRequest;
import com.api.readinglog.domain.member.controller.dto.request.LoginRequest;
import com.api.readinglog.domain.member.controller.dto.request.UpdatePasswordRequest;
import com.api.readinglog.domain.member.controller.dto.request.UpdateProfileRequest;
import com.api.readinglog.domain.member.controller.dto.response.MemberDetailsResponse;
import com.api.readinglog.domain.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
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
    private final EmailService emailService;

    @PostMapping("/join-nickname")
    public Response<Void> join_nickname(@ModelAttribute @Valid JoinNicknameRequest request) {
        memberService.joinNickname(request);
        return Response.success(HttpStatus.OK, "닉네임 검사 통과!");
    }

    @PostMapping("/join")
    public Response<Void> join(@ModelAttribute @Valid JoinRequest request) {
        memberService.join(request);
        return Response.success(HttpStatus.CREATED, "회원 가입 완료");
    }

    @PostMapping("/login")
    public Response<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtToken jwtToken = memberService.login(request);
        response.addHeader("Authorization", jwtToken.getAccessToken());
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

    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = extractRefreshToken(request);
        memberService.logout(refreshToken, response);
        return Response.success(HttpStatus.OK, "로그아웃 성공!");
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

    @GetMapping("/reissue")
    public Response<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 리프레시 토큰 가져오기
        String refreshToken = extractRefreshToken(request);

        // 리프레시 토큰을 사용하여 새로운 토큰 재발급
        JwtToken newToken = memberService.reissueToken(refreshToken);

        // 재발급된 토큰 반환
        response.addHeader("Authorization", newToken.getAccessToken());
        CookieUtils.addCookie(response, "refreshToken", newToken.getRefreshToken(), 24 * 60 * 60 * 7);
        return Response.success(HttpStatus.OK, "토큰 재발급 성공!");
    }

    @PatchMapping("/password")
    public Response<Void> updatePassword(@AuthenticationPrincipal CustomUserDetail user,
                                         @RequestBody @Valid UpdatePasswordRequest request) {
        memberService.updatePassword(user.getId(), request);
        return Response.success(HttpStatus.OK, "비밀번호 변경 성공!");
    }

    @PostMapping("/send-authCode")
    public Response<Void> sendEmailAuthCode(@RequestBody @Valid EmailRequest request) {
        emailService.sendAuthCode(request.getEmail());
        return Response.success(HttpStatus.OK, "이메일 인증 코드 전송 완료!");
    }

    @PostMapping("/verify-authCode")
    public Response<Void> verifyAuthCode(@RequestBody @Valid AuthCodeVerificationRequest request) {
        emailService.verifyAuthCode(request.getEmail(), request.getAuthCode());
        return Response.success(HttpStatus.OK, "이메일 인증 성공!");
    }

    // HttpServletRequest에서 리프레시 토큰을 추출하는 메서드
    private String extractRefreshToken(HttpServletRequest request) {
        // 쿠키에서 리프레`시 토큰 이름으로 검색
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        throw new IllegalStateException("리프레시 토큰이 쿠키에 없습니다.");
    }
}
