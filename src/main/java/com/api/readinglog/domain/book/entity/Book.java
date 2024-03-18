package com.api.readinglog.domain.book.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
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

    @Column(name = "book_title", nullable = false)
    private String title; // 제목

    @Column(name = "book_author", nullable = false)
    private String author; // 저자

    @Column(name = "book_publisher", nullable = false)
    private String publisher; // 출판사

    @Column(name = "book_description", columnDefinition = "text")
    private String description;

    @Column(name = "book_cover")
    private String cover;
}
