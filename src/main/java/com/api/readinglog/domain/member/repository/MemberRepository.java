package com.api.readinglog.domain.member.repository;

import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndRole(String email, MemberRole role);
}
