package com.api.readinglog.domain.book.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.book.dto.BookDirectRequest;
import com.api.readinglog.domain.book.dto.BookRegisterRequest;
import com.api.readinglog.domain.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@SQLDelete(sql = "UPDATE book SET deleted_at = NOW() WHERE book_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Book extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "book_item_id", unique = true)
    private Integer itemId; // 책 고유 번호

    @Column(name = "book_title", nullable = false)
    private String title; // 제목

    @Column(name = "book_author", nullable = false)
    private String author; // 저자

    @Column(name = "book_publisher", nullable = false)
    private String publisher; // 출판사

    @Column(name = "book_cover")
    private String cover;

    @Builder
    private Book(Member member, Integer itemId, String title, String author, String publisher, String cover) {
        this.member = member;
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.cover = cover;
    }

    public static Book of(Member member, BookRegisterRequest request) {
        return Book.builder()
                .member(member)
                .itemId(request.getItemId())
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .cover(request.getCover())
                .build();
    }

    public static Book of(Member member, BookDirectRequest request) {
        return Book.builder()
                .member(member)
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .cover(request.getCover().getOriginalFilename()) // TODO: s3 업로드
                .build();
    }

}
