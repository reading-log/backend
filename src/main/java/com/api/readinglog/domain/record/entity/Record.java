package com.api.readinglog.domain.record.entity;


import com.api.readinglog.common.base.BaseTimeEntity;
import com.api.readinglog.domain.book.entity.Book;
import com.api.readinglog.domain.member.entity.Member;
import com.api.readinglog.domain.record.controller.dto.RecordWriteRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE record SET deleted_at = NOW() WHERE record_id = ?")
@Where(clause = "deleted_at IS NULL")
public class Record extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "record_start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "record_end_date", nullable = false)
    private LocalDateTime endDate;

    @Builder
    public Record(Member member, Book book, LocalDateTime startDate, LocalDateTime endDate) {
        this.member = member;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static Record of(Member member, Book book, RecordWriteRequest request) {
        return Record.builder()
                .member(member)
                .book(book)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();
    }

}
