package com.api.readinglog.domain.member.service;

import com.api.readinglog.domain.member.controller.dto.JoinRequest;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void join(JoinRequest request) {
        memberRepository.findByEmail(request.getEmail()).ifPresent(it -> {
            throw new RuntimeException("이미 존재하는 회원입니다.");
        });

        String password = request.getPassword();
        String passwordConfirm = request.getPasswordConfirm();

        if(!password.equals(passwordConfirm)) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = passwordEncoder.encode(password);
        Member member = Member.of(request, encodedPassword);

        /* TODO: 이미지 파일 S3에 업로드*/

        memberRepository.save(member);
    }
}