package com.api.readinglog.domain.record.repository;

import com.api.readinglog.domain.record.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Long> {
}
