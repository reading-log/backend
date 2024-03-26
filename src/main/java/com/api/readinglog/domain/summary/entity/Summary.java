package com.api.readinglog.domain.summary.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.summary.controller.dto.request.ModifyRequest;
import com.api.readinglog.domain.summary.controller.dto.request.WriteRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE summary SET deleted_at = NOW() WHERE summary_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Summary extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "summary_content", nullable = false, length = 100)
    private String content;

    @Builder
    private Summary(Member member, Book book, String content) {
        this.member = member;
        this.book = book;
        this.content = content;
    }

    public static Summary of(Member member, Book book, WriteRequest request) {
        return Summary.builder()
                .member(member)
                .book(book)
                .content(request.getContent())
                .build();
    }

    public void modify(ModifyRequest request) {
        this.content = request.getContent();
    }
}
