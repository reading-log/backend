package com.api.readinglog.domain.hightlight.repository;

import com.api.readinglog.domain.hightlight.entity.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HighlightRepository extends JpaRepository<Highlight, Long> {
}
