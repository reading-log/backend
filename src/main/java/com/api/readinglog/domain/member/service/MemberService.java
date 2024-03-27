package com.api.readinglog.domain.member.service;

import com.api.readinglog.common.aws.AmazonS3Service;
import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.MemberException;
import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.jwt.JwtTokenProvider;
import com.api.readinglog.common.oauth.OAuth2RevokeService;
import com.api.readinglog.common.security.util.JwtUtils;
import com.api.readinglog.domain.member.controller.dto.request.DeleteRequest;
import com.api.readinglog.domain.member.controller.dto.request.JoinNicknameRequest;
import com.api.readinglog.domain.member.controller.dto.request.JoinRequest;
import com.api.readinglog.domain.member.controller.dto.request.LoginRequest;
import com.api.readinglog.domain.member.controller.dto.request.UpdatePasswordRequest;
import com.api.readinglog.domain.member.controller.dto.request.UpdateProfileRequest;
import com.api.readinglog.domain.member.controller.dto.response.MemberDetailsResponse;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import com.api.readinglog.domain.token.repository.SocialAccessTokenRepository;
import com.api.readinglog.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final SocialAccessTokenRepository socialAccessTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AmazonS3Service amazonS3Service;
    private final OAuth2RevokeService oAuth2RevokeService;
    private final JwtUtils jwtUtils;

    public void joinNickname(JoinNicknameRequest request) {
        validateExistingNickname(request.getNickname());
    }

    public void join(JoinRequest request) {
        validateExistingMember(request.getEmail());
        validateExistingNickname(request.getNickname());
        validatePassword(request.getPassword(), request.getPasswordConfirm());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String uploadFileName = determineProfileImageUrl(request.getProfileImage());

        Member member = Member.of(request, encodedPassword, uploadFileName);
        log.debug("회원 프로필 사진 이름: {}", uploadFileName);
        memberRepository.save(member);
    }

    public JwtToken login(LoginRequest request) {
        Authentication authentication = getUserAuthentication(request);
        return jwtTokenProvider.generateToken(authentication);
    }

    public Member getMemberByEmailAndRole(String email, MemberRole role) {
        return memberRepository.findByEmailAndRole(email, role)
                .orElseThrow(() -> new UsernameNotFoundException(ErrorCode.NOT_FOUND_MEMBER.getMessage()));
    }

    public Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new MemberException(ErrorCode.NOT_FOUND_MEMBER));
    }

    public MemberDetailsResponse getMemberDetails(Long memberId) {
        Member member = getMemberById(memberId);
        String profileImg = member.getProfileImg();

        // 소셜 로그인 한 회원이 아닌 경우에만 S3에서 이미지 객체 URL을 가져옴
        if (member.getRole().equals(MemberRole.MEMBER_NORMAL)) {
            profileImg = amazonS3Service.getFileUrl(member.getProfileImg());
        }
        return MemberDetailsResponse.of(member, profileImg);
    }

    public void updateProfile(Long memberId, UpdateProfileRequest request) {
        Member member = getMemberById(memberId);

        // 기존 이미지를 기본 값으로 설정
        String updatedFileName = member.getProfileImg();

        if (!isEmptyProfileImg(request.getProfileImg())) {
            // 수정할 이미지 데이터가 존재할 경우, 기존 이미지 삭제 후 새 이미지 업로드
            amazonS3Service.deleteFile(member.getProfileImg());
            updatedFileName = amazonS3Service.uploadFile(request.getProfileImg());
        }
        member.updateProfile(request.getNickname(), updatedFileName);
    }

    public void logout(String token, HttpServletResponse response) {
        jwtUtils.handleLogout(token, response);
    }

    public void deleteMember(Long memberId, DeleteRequest request) {
        Member member = getMemberById(memberId);
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new MemberException(ErrorCode.PASSWORD_MISMATCH);
        }
        memberRepository.deleteById(memberId);
    }

    public void deleteSocialMember(Long memberId) {
        Member member = getMemberById(memberId);

        socialAccessTokenRepository.findByMember(member).ifPresent(accessToken -> {
            String socialAccessToken = accessToken.getSocialAccessToken();
            revokeSocialAccessToken(member, socialAccessToken);
            socialAccessTokenRepository.delete(accessToken); // SocialAccessToken 삭제
        });

        memberRepository.deleteById(member.getId()); // 회원 정보 삭제
    }

    public JwtToken reissueToken(String refreshToken) {
        return jwtTokenProvider.reissueTokenByRefreshToken(refreshToken);
    }

    public void updatePassword(Long memberId, UpdatePasswordRequest request) {
        Member member = getMemberById(memberId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new MemberException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }

        validatePassword(request.getNewPassword(), request.getNewPasswordConfirm());
        member.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    // 소셜 계정 연동 해제 처리를 별도의 메서드로 추출
    private void revokeSocialAccessToken(Member member, String socialAccessToken) {
        switch (member.getRole()) {
            case MEMBER_KAKAO -> oAuth2RevokeService.revokeKakao(socialAccessToken);
            case MEMBER_GOOGLE -> oAuth2RevokeService.revokeGoogle(socialAccessToken);
            case MEMBER_NAVER -> oAuth2RevokeService.revokeNaver(socialAccessToken);
        }
    }

    private void validateExistingMember(String email) {
        if (memberRepository.findByEmailAndRole(email, MemberRole.MEMBER_NORMAL).isPresent()) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }
    }

    private void validateExistingNickname(String nickname) {
        if (memberRepository.findByNickname(nickname).isPresent()) {
            throw new MemberException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new MemberException(ErrorCode.PASSWORD_MISMATCH);
        }
    }

    private Authentication getUserAuthentication(LoginRequest request) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    request.getEmail(), request.getPassword());
            return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new MemberException(ErrorCode.UNAUTHORIZED_LOGIN);
        }
    }

    private String determineProfileImageUrl(MultipartFile profileImage) {
        /* TODO: 프로필 사진 요청이 없는 경우, 기본 프로필 저장 */
        if (profileImage == null || profileImage.isEmpty()) {
            return "기본 프로필 이미지 URL";
        } else {
            return amazonS3Service.uploadFile(profileImage);
        }
    }

    private boolean isEmptyProfileImg(MultipartFile profileImg) {
        return (profileImg == null || profileImg.isEmpty());
    }
}
