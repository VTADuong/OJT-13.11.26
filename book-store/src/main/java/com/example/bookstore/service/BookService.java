package com.example.bookstore.service;

import com.example.bookstore.dto.BookFormDto;
import com.example.bookstore.dto.BookSearchCriteria;
import com.example.bookstore.entity.Book;
import org.springframework.data.domain.Page;

import java.util.List;

public interface BookService {

    Page<Book> searchBooks(BookSearchCriteria criteria);

    List<Book> findAllBooks();

    Book findById(Long id);

    Book createBook(BookFormDto formDto);

    Book updateBook(Long id, BookFormDto formDto);

    void deleteBook(Long id);
}

