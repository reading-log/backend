package com.api.readinglog.domain.review.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.review.controller.dto.request.WriteRequest;
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
@SQLDelete(sql = "UPDATE review SET deleted_at = NOW() WHERE review_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "review_content", nullable = false, columnDefinition = "text")
    private String content;

    @Builder
    public Review(Member member, Book book, String content) {
        this.member = member;
        this.book = book;
        this.content = content;
    }

    public static Review of(Member member, Book book, WriteRequest request) {
        return Review.builder()
                .member(member)
                .book(book)
                .content(request.getContent())
                .build();
    }


}
