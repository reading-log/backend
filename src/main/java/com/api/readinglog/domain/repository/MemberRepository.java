package com.api.readinglog.domain.repository;

import com.api.readinglog.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
}
