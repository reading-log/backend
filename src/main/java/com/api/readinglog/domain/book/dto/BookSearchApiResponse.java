package com.api.readinglog.domain.book.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookSearchApiResponse {

    private int totalResults; // 전체 데이터 개수
    private int itemsPerPage; // 페이지당 아이템 개수
    private int totalPage; // 전체 페이지수
    private int startIndex; // 시작 페이지
    private boolean hasNext; // 다음 페이지 존재 여부
    private String query; // 검색어

    private List<Item> item = new ArrayList<>();

    @Getter
    @Setter
    static class Item {
        private String itemId; // 책 고유 번호
        private String title;
        private String author;
        private String publisher;
        private String cover;
    }
}

