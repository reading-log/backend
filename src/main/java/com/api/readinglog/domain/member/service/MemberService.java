package com.api.readinglog.domain.member.service;

import com.api.readinglog.common.aws.AmazonS3Service;
import com.api.readinglog.common.exception.ErrorCode;
import com.api.readinglog.common.exception.custom.MemberException;
import com.api.readinglog.common.jwt.JwtToken;
import com.api.readinglog.common.jwt.JwtTokenProvider;
import com.api.readinglog.domain.member.controller.dto.JoinRequest;
import com.api.readinglog.domain.member.controller.dto.LoginRequest;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import com.api.readinglog.domain.member.repository.MemberRepository;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final AmazonS3Service amazonS3Service;

    public void join(JoinRequest request) {
        validateExistingMember(request.getEmail(), request.getNickname());
        validatePassword(request.getPassword(), request.getPasswordConfirm());

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        String uploadFileUrl = determineProfileImageUrl(request.getProfileImage());

        Member member = Member.of(request, encodedPassword, uploadFileUrl);
        log.debug("회원 프로필 사진 객체 URL: {}", uploadFileUrl);
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

    private void validateExistingMember(String email, String nickname) {
        if (memberRepository.findByEmailAndRole(email, MemberRole.MEMBER_NORMAL).isPresent()) {
            throw new MemberException(ErrorCode.MEMBER_ALREADY_EXISTS);
        }

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
}
