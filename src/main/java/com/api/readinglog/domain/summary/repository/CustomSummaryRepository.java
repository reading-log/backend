package com.api.readinglog.domain.summary.repository;

import com.api.readinglog.domain.summary.entity.Summary;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface CustomSummaryRepository {

    List<Summary> findByBookTitle(String bookTitle, Pageable pageable);

    List<Summary> findByCategoryName(String categoryTitle, Pageable pageable);
}
