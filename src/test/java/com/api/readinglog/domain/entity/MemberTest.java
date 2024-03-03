package com.api.readinglog.domain.entity;

import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.member.entity.MemberRole;
import com.api.readinglog.domain.member.repository.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class MemberTest {

    @Autowired
    private MemberRepository repository;

    @Rollback(value = false)
    @Test
    @Transactional
    public void test() {
        Member member = Member.of("test@test.com", "test", "test", MemberRole.MEMBER);

        Member saveMember = repository.save(member);
        repository.delete(saveMember);

        List<Member> members = repository.findAll();
        System.out.println("size: " + members.size());
    }
}