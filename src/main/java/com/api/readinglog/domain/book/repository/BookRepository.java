package com.api.readinglog.domain.book.repository;

import com.api.readinglog.domain.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>, CustomBookRepository {

}
