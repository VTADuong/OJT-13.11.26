package com.example.bookstore.service.impl;

import com.example.bookstore.dto.BookFormDto;
import com.example.bookstore.dto.BookSearchCriteria;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.BorrowRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.BookService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
        "id", "code", "title", "author", "publishYear", "quantity", "createdAt"
    );

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;
    private final BorrowRepository borrowRepository;

    public BookServiceImpl(BookRepository bookRepository,
                           CategoryRepository categoryRepository,
                           BorrowRepository borrowRepository) {
        this.bookRepository = bookRepository;
        this.categoryRepository = categoryRepository;
        this.borrowRepository = borrowRepository;
    }

    @Override
    public Page<Book> searchBooks(BookSearchCriteria criteria) {
        String sortField = ALLOWED_SORT_FIELDS.contains(criteria.getSortBy())
            ? criteria.getSortBy()
            : "title";
        Sort.Direction direction = "desc".equalsIgnoreCase(criteria.getSortDir())
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;

        Pageable pageable = PageRequest.of(
            Math.max(criteria.getPage(), 0),
            criteria.getSize() > 0 ? criteria.getSize() : 6,
            Sort.by(direction, sortField)
        );

        String keyword = criteria.getKeyword() != null ? criteria.getKeyword().trim() : "";
        return bookRepository.searchBooks(keyword, criteria.getCategoryId(), pageable);
    }

    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAllByOrderByTitleAsc();
    }

    @Override
    public Book findById(Long id) {
        return bookRepository.findWithCategoryById(id)
            .orElseThrow(() -> new ResourceNotFoundException("sách", id));
    }

    @Override
    @Transactional
    public Book createBook(BookFormDto formDto) {
        String normalizedCode = formDto.getCode() != null ? formDto.getCode().trim().toUpperCase() : "";
        if (bookRepository.existsByCodeIgnoreCase(normalizedCode)) {
            throw new DuplicateResourceException("code", "Mã sách \"" + normalizedCode + "\" đã tồn tại.");
        }

        Category category = categoryRepository.findById(formDto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("thể loại sách", formDto.getCategoryId()));

        Book book = new Book(
            normalizedCode,
            formDto.getTitle().trim(),
            formDto.getAuthor().trim(),
            formDto.getPublishYear(),
            formDto.getQuantity(),
            category
        );

        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Long id, BookFormDto formDto) {
        Book existing = findById(id);
        String normalizedCode = formDto.getCode() != null ? formDto.getCode().trim().toUpperCase() : "";

        if (bookRepository.existsByCodeIgnoreCaseAndIdNot(normalizedCode, id)) {
            throw new DuplicateResourceException("code", "Mã sách \"" + normalizedCode + "\" đã được sử dụng cho sách khác.");
        }

        Category category = categoryRepository.findById(formDto.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("thể loại sách", formDto.getCategoryId()));

        existing.setCode(normalizedCode);
        existing.setTitle(formDto.getTitle().trim());
        existing.setAuthor(formDto.getAuthor().trim());
        existing.setPublishYear(formDto.getPublishYear());
        existing.setQuantity(formDto.getQuantity());
        existing.setCategory(category);

        return bookRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book existing = findById(id);
        if (borrowRepository.existsByBookIdAndReturnDateIsNull(id)) {
            throw new BusinessException("Không thể xoá sách \"" + existing.getTitle()
                + "\" vì sách đang có người mượn chưa trả.");
        }
        if (borrowRepository.existsByBookId(id)) {
            throw new BusinessException("Không thể xoá sách \"" + existing.getTitle()
                + "\" vì sách đã phát sinh dữ liệu trong lịch sử mượn trả.");
        }
        bookRepository.delete(existing);
    }
}

