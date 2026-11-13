package com.example.bookstore.service;

import com.example.bookstore.dto.BookFormDto;
import com.example.bookstore.dto.BookSearchCriteria;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Category;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.BorrowRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BorrowRepository borrowRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Công nghệ thông tin");
    }

    @Test
    @DisplayName("Unit Test 7: Thêm sách mới thành công khi mã sách không trùng lặp")
    void createBook_ShouldSucceed_WhenCodeIsUniqueAndCategoryExists() {
        BookFormDto formDto = new BookFormDto(
            null,
            "bk-cntt-010",
            "Domain-Driven Design",
            "Eric Evans",
            2003,
            5,
            1L
        );

        when(bookRepository.existsByCodeIgnoreCase("BK-CNTT-010")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(bookRepository.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(10L);
            return b;
        });

        Book created = bookService.createBook(formDto);

        assertNotNull(created);
        assertEquals("BK-CNTT-010", created.getCode());
        assertEquals("Domain-Driven Design", created.getTitle());
        assertEquals(5, created.getQuantity());
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    @DisplayName("Unit Test 8: Thêm sách thất bại khi mã sách đã tồn tại")
    void createBook_ShouldThrowDuplicateResourceException_WhenCodeAlreadyExists() {
        BookFormDto formDto = new BookFormDto(
            null,
            "BK-CNTT-001",
            "Clean Code",
            "Robert C. Martin",
            2008,
            5,
            1L
        );

        when(bookRepository.existsByCodeIgnoreCase("BK-CNTT-001")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(
            DuplicateResourceException.class,
            () -> bookService.createBook(formDto)
        );

        assertEquals("code", ex.getFieldName());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Unit Test 9: Không cho phép xoá sách khi sách đang có người mượn chưa trả")
    void deleteBook_ShouldThrowBusinessException_WhenBookHasActiveBorrows() {
        Book book = new Book("BK-CNTT-001", "Clean Code", "Robert C. Martin", 2008, 2, category);
        book.setId(1L);

        when(bookRepository.findWithCategoryById(1L)).thenReturn(Optional.of(book));
        when(borrowRepository.existsByBookIdAndReturnDateIsNull(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> bookService.deleteBook(1L));
        verify(bookRepository, never()).delete(any(Book.class));
    }

    @Test
    @DisplayName("Unit Test 10: Tìm kiếm, lọc theo thể loại và phân trang danh sách sách")
    void searchBooks_ShouldReturnPagedBooks_WithSortingAndFiltering() {
        Book book = new Book("BK-CNTT-001", "Clean Code", "Robert C. Martin", 2008, 5, category);
        Page<Book> mockPage = new PageImpl<>(List.of(book));

        when(bookRepository.searchBooks(eq("Clean"), eq(1L), any(Pageable.class))).thenReturn(mockPage);

        BookSearchCriteria criteria = new BookSearchCriteria("Clean", 1L, 0, 6, "title", "asc");
        Page<Book> result = bookService.searchBooks(criteria);

        assertEquals(1, result.getTotalElements());
        assertEquals("Clean Code", result.getContent().get(0).getTitle());
    }
}

