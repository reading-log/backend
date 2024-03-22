package com.api.readinglog.common.security;

import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import com.api.readinglog.domain.member.repository.MemberRepository;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("일반 로그인 시작: {}", email);

        Member member = memberRepository.findByEmailAndRole(email, MemberRole.MEMBER_NORMAL)
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 회원을 찾을 수 없습니다."));
        // 권한 설정 후 UserDetails 객체 생성 및 반환
        GrantedAuthority authority = new SimpleGrantedAuthority(member.getRole().name());
        // User가 아닌 CustomUserDetail 반환
        return new CustomUserDetail(member.getEmail(), member.getPassword(), member.getId(), Collections.singleton(authority));
    }
}
