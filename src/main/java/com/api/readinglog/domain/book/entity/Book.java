package com.api.readinglog.domain.book.entity;

import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.book.dto.BookDirectRequest;
import com.api.readinglog.domain.book.dto.BookModifyRequest;
import com.api.readinglog.domain.book.dto.BookRegisterRequest;
import com.api.readinglog.domain.highlight.entity.Highlight;
import com.api.readinglog.domain.highlight.entity.Highlight;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.record.entity.Record;
import com.api.readinglog.domain.review.entity.Review;
import com.api.readinglog.domain.summary.entity.Summary;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "book_category", nullable = false)
    private String category; // 카테고리

    @Column(name = "book_cover")
    private String cover;

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Summary> summaryList = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Highlight> HighlightList = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Record> recordList = new ArrayList<>();

    @Builder
    private Book(Member member, Integer itemId, String title, String author, String publisher, String category, String cover) {
        this.member = member;
        this.itemId = itemId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
        this.cover = cover;
    }

    public static Book of(Member member, BookRegisterRequest request) {
        return Book.builder()
                .member(member)
                .itemId(request.getItemId())
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .category(request.getCategory())
                .cover(request.getCover())
                .build();
    }

    public static Book of(Member member, BookDirectRequest request, String cover) {
        return Book.builder()
                .member(member)
                .title(request.getTitle())
                .author(request.getAuthor())
                .publisher(request.getPublisher())
                .category(request.getCategory())
                .cover(cover)
                .build();
    }

    public void modify(BookModifyRequest request, String cover) {
        this.title = request.getTitle();
        this.author = request.getAuthor();
        this.publisher = request.getPublisher();
        this.category = request.getCategory();
        this.cover = cover;
    }
}
