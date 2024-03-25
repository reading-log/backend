package com.api.readinglog.domain.hightlight.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@SQLDelete(sql = "UPDATE highlight SET deleted_at = NOW() WHERE highlight_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Highlight extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highlight_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "highlight_content", nullable = false, length = 500)
    private String content;

    @Column(name = "highlight_status", nullable = false)
    private boolean status;

    @Builder
    public Highlight(Member member, Book book, String content, boolean status) {
        this.member = member;
        this.book = book;
        this.content = content;
        this.status = status;
    }

    public static Highlight of(Member member, Book book, String content) {
        return Highlight.builder()
                .member(member)
                .book(book)
                .content(content)
                .build();
    }
}
