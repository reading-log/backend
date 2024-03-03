package com.api.readinglog.domain.member.repository;

import com.api.readinglog.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
