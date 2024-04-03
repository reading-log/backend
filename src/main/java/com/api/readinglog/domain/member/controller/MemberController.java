package com.api.readinglog.domain.member.controller;

import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.response.Response;
import com.api.readinglog.common.security.CustomUserDetail;
import com.api.readinglog.common.security.util.CookieUtils;
import com.api.readinglog.domain.email.dto.AuthCodeVerificationRequest;
import com.api.readinglog.domain.email.dto.EmailRequest;
import com.api.readinglog.domain.email.service.EmailService;
import com.api.readinglog.domain.like.service.LikeSummaryService;
import com.api.readinglog.domain.member.controller.dto.request.DeleteRequest;
import com.api.readinglog.domain.member.controller.dto.request.JoinNicknameRequest;
import com.api.readinglog.domain.member.controller.dto.request.JoinRequest;
import com.api.readinglog.domain.member.controller.dto.request.LoginRequest;
import com.api.readinglog.domain.member.controller.dto.request.UpdatePasswordRequest;
import com.api.readinglog.domain.member.controller.dto.request.UpdateProfileRequest;
import com.api.readinglog.domain.member.controller.dto.response.MemberDetailsResponse;
import com.api.readinglog.domain.member.service.MemberService;
import com.api.readinglog.domain.summary.controller.dto.response.SummaryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
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

@Tag(name = "Members", description = "회원 API 목록입니다.")
@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailService emailService;
    private final LikeSummaryService likeSummaryService;

    @Operation(summary = "닉네임 중복 검사", description = "회원 가입 전, 닉네임 중복을 검사합니다.",
            parameters = {
                    @Parameter(name = "nickname", description = "닉네임", required = true,
                            schema = @Schema(type = "string", implementation = String.class))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 중복 검사 통과",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "닉네임 중복 검사 실패")
    })
    @PostMapping("/join-nickname")
    public Response<Void> join_nickname(@ModelAttribute @Valid JoinNicknameRequest request) {
        memberService.joinNickname(request);
        return Response.success(HttpStatus.OK, "닉네임 중복 검사 통과");
    }

    @Operation(summary = "회원 가입", description = "일반 이메일 회원 가입입니다.",
            parameters = {
                    @Parameter(name = "email", description = "이메일", example = "test@test.com", required = true,
                            schema = @Schema(type = "string", implementation = String.class)),
                    @Parameter(name = "password", description = "비밀번호", example = "Password123!", required = true,
                            schema = @Schema(type = "string", implementation = String.class)),
                    @Parameter(name = "passwordConfirm", description = "비밀번호 확인", example = "Password123!", required = true,
                            schema = @Schema(type = "string", implementation = String.class)),
                    @Parameter(name = "nickname", description = "닉네임", example = "테스트닉네임", required = true,
                            schema = @Schema(type = "string", implementation = String.class)),
                    @Parameter(name = "profileImage", description = "프로필 이미지", required = false,
                            schema = @Schema(type = "string", implementation = String.class)),
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원 가입 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "회원 가입 실패 (필수 입력값을 입력하지 않은 경우, 비밀번호와 비밀번호 확인이 일치하지 않는 경우)")
    })
    @PostMapping("/join")
    public Response<Void> join(@ModelAttribute @Valid JoinRequest request) {
        memberService.join(request);
        return Response.success(HttpStatus.CREATED, "회원 가입 성공");
    }

    @Operation(summary = "로그인", description = "일반 로그인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "로그인 실패: 인증에 실패하였습니다.")
    })
    @PostMapping("/login")
    public Response<Void> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        JwtToken jwtToken = memberService.login(request);
        response.addHeader("Authorization", jwtToken.getAccessToken());
        CookieUtils.addCookie(response, "refreshToken", jwtToken.getRefreshToken(), 24 * 60 * 60 * 7);
        return Response.success(HttpStatus.OK, "로그인 성공");
    }

    @Operation(summary = "회원 정보 조회", description = "인증 토큰을 사용하여 회원 정보를 조회합니다.")
    @GetMapping("/me")
    public Response<MemberDetailsResponse> findMember(@AuthenticationPrincipal CustomUserDetail user) {
        MemberDetailsResponse member = memberService.getMemberDetails(user.getId());
        return Response.success(HttpStatus.OK, "회원 조회 성공", member);
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.",
            parameters = {
                    @Parameter(name = "nickname", description = "닉네임", example = "새로운닉네임", required = true,
                            schema = @Schema(type = "string", implementation = String.class)),
                    @Parameter(name = "profileImage", description = "프로필 이미지", required = false,
                            schema = @Schema(type = "string", implementation = String.class))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 수정 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "401", description = "로그인 실패: 인증에 실패하였습니다.")
    })
    @PatchMapping("/me")
    public Response<Void> updateProfile(@AuthenticationPrincipal CustomUserDetail user,
                                        @ModelAttribute @Valid UpdateProfileRequest request) {
        memberService.updateProfile(user.getId(), request);
        return Response.success(HttpStatus.OK, "회원 수정 성공");
    }

    @Operation(summary = "로그아웃", description = "쿠키에 저장된 리프레시 토큰을 통해 로그아웃 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰이 쿠키에 없습니다.")
    })
    @PostMapping("/logout")
    public Response<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.extractRefreshToken(request);
        memberService.logout(refreshToken, response);
        return Response.success(HttpStatus.OK, "로그아웃 성공");
    }

    @Operation(summary = "일반 회원 탈퇴", description = "일반 회원은 비밀번호 확인을 통해 회원 검증 후, 서비스를 탈퇴할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "일반 회원 탈퇴 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "회원이 존재하지 않습니다!")
    })
    @DeleteMapping("/me")
    public Response<Void> deleteMember(@AuthenticationPrincipal CustomUserDetail user,
                                       @RequestBody DeleteRequest request) {
        memberService.deleteMember(user.getId(), request);
        return Response.success(HttpStatus.OK, "일반 회원 탈퇴 성공");
    }

    @Operation(summary = "소셜 회원 탈퇴", description = "소셜 회원은 재로그인을 통해 회원 검증 후, 재발급 받은 액세스 토큰을 통해 서비스를 탈퇴할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "소셜 회원 탈퇴 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "회원이 존재하지 않습니다!")
    })
    @DeleteMapping("/social/me")
    public Response<Void> deleteSocialMember(@AuthenticationPrincipal CustomUserDetail user) {
        memberService.deleteSocialMember(user.getId());
        return Response.success(HttpStatus.OK, "소셜 회원 탈퇴 성공");
    }

    @Operation(summary = "토큰 재발급", description = "액세스 토큰이 만료된 경우, 리프레시 토큰을 이용하여 재발급 받을 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "리프레시 토큰이 쿠키에 없습니다.")
    })
    @GetMapping("/reissue")
    public Response<Void> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = CookieUtils.extractRefreshToken(request);
        JwtToken newToken = memberService.reissueToken(refreshToken);
        response.addHeader("Authorization", newToken.getAccessToken());
        CookieUtils.addCookie(response, "refreshToken", newToken.getRefreshToken(), 24 * 60 * 60 * 7);
        return Response.success(HttpStatus.OK, "토큰 재발급 성공");
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "비밀번호 변경 실패")
    })
    @PatchMapping("/password")
    public Response<Void> updatePassword(@AuthenticationPrincipal CustomUserDetail user,
                                         @RequestBody @Valid UpdatePasswordRequest request) {
        memberService.updatePassword(user.getId(), request);
        return Response.success(HttpStatus.OK, "비밀번호 변경 성공");
    }

    @Operation(summary = "이메일 인증 코드 전송", description = "사용자 이메일로 인증 코드를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 코드 전송 완료",
                    content = {@Content(schema = @Schema(implementation = Response.class))})
    })
    @PostMapping("/send-authCode")
    public Response<Void> sendEmailAuthCode(@RequestBody @Valid EmailRequest request) {
        emailService.sendAuthCode(request.getEmail());
        return Response.success(HttpStatus.OK, "이메일 인증 코드 전송 완료");
    }

    @Operation(summary = "이메일 인증", description = "사용자 이메일로 보낸 인증 코드를 검증하여 이메일을 인증합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공",
                    content = {@Content(schema = @Schema(implementation = Response.class))}),
            @ApiResponse(responseCode = "400", description = "이메일 인증에 실패하였습니다.")
    })
    @PostMapping("/verify-authCode")
    public Response<Void> verifyAuthCode(@RequestBody @Valid AuthCodeVerificationRequest request) {
        emailService.verifyAuthCode(request.getEmail(), request.getAuthCode());
        return Response.success(HttpStatus.OK, "이메일 인증 성공");
    }

    @Operation(summary = "임시 비밀번호 전송", description = "사용자 이메일로 임시 비밀번호를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "임시 비밀번호 전송 완료",
                    content = {@Content(schema = @Schema(implementation = Response.class))})
    })
    @PostMapping("/send-temporaryPassword")
    public Response<Void> sendEmailTempPassword(@AuthenticationPrincipal CustomUserDetail user,
                                                @RequestBody @Valid EmailRequest request) {
        emailService.sendTemporaryPassword(user.getId(), request.getEmail());
        return Response.success(HttpStatus.OK, "임시 비밀번호 전송 완료");
    }

    @GetMapping("/likes/summaries")
    public Response<List<SummaryResponse>> getLikeSummaries(@AuthenticationPrincipal CustomUserDetail user) {
        List<SummaryResponse> likeSummaries = likeSummaryService.getLikeSummaries(user.getId());
        return Response.success(HttpStatus.OK, "내가 좋아요 한 한줄평 목록 조회", likeSummaries);
    }
}
