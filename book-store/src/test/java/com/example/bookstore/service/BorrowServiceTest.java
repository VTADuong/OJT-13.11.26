package com.example.bookstore.service;

import com.example.bookstore.dto.BorrowFormDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Borrow;
import com.example.bookstore.entity.Category;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.BorrowRepository;
import com.example.bookstore.service.impl.BorrowServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BorrowServiceTest {

    @Mock
    private BorrowRepository borrowRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BorrowServiceImpl borrowService;

    private Category category;
    private Book availableBook;
    private Book outOfStockBook;

    @BeforeEach
    void setUp() {
        category = new Category(1L, "Công nghệ thông tin");

        availableBook = new Book("BK-CNTT-001", "Clean Code", "Robert C. Martin", 2008, 3, category);
        availableBook.setId(10L);

        outOfStockBook = new Book("BK-CNTT-003", "Spring in Action", "Craig Walls", 2022, 0, category);
        outOfStockBook.setId(20L);
    }

    @Test
    @DisplayName("Unit Test 1: Mượn sách thành công khi sách còn tồn kho và email chưa mượn trùng")
    void borrowBook_ShouldSucceedAndDecreaseQuantity_WhenBookAvailableAndNoActiveDuplicateBorrow() {
        LocalDate today = LocalDate.now();
        BorrowFormDto formDto = new BorrowFormDto(
            10L,
            "Nguyễn Văn An",
            "An.Nguyen@Example.com",
            today,
            today.plusDays(14)
        );

        when(bookRepository.findWithCategoryById(10L)).thenReturn(Optional.of(availableBook));
        when(borrowRepository.existsByBookIdAndBorrowerEmailIgnoreCaseAndReturnDateIsNull(10L, "an.nguyen@example.com"))
            .thenReturn(false);
        when(borrowRepository.save(any(Borrow.class))).thenAnswer(invocation -> {
            Borrow saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        Borrow result = borrowService.borrowBook(formDto);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(2, availableBook.getQuantity(), "Số lượng sách trong kho phải giảm đi 1 sau khi mượn");
        assertEquals("an.nguyen@example.com", result.getBorrowerEmail());
        verify(bookRepository).save(availableBook);
        verify(borrowRepository).save(any(Borrow.class));
    }

    @Test
    @DisplayName("Unit Test 2: Hết sách (quantity = 0) thì không cho mượn và ném BusinessException")
    void borrowBook_ShouldThrowBusinessException_WhenBookIsOutOfStock() {
        LocalDate today = LocalDate.now();
        BorrowFormDto formDto = new BorrowFormDto(
            20L,
            "Trần Thị Bích",
            "bich.tran@example.com",
            today,
            today.plusDays(7)
        );

        when(bookRepository.findWithCategoryById(20L)).thenReturn(Optional.of(outOfStockBook));

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> borrowService.borrowBook(formDto)
        );

        assertEquals("bookId", exception.getFieldName());
        assertTrue(exception.getMessage().contains("đã hết trong kho"));
        assertEquals(0, outOfStockBook.getQuantity());
        verify(borrowRepository, never()).save(any(Borrow.class));
    }

    @Test
    @DisplayName("Unit Test 3: Một email không được mượn trùng cùng một cuốn sách khi chưa trả")
    void borrowBook_ShouldThrowDuplicateResourceException_WhenSameEmailHasUnreturnedBorrowForSameBook() {
        LocalDate today = LocalDate.now();
        BorrowFormDto formDto = new BorrowFormDto(
            10L,
            "Nguyễn Văn An",
            "an.nguyen@example.com",
            today,
            today.plusDays(10)
        );

        when(bookRepository.findWithCategoryById(10L)).thenReturn(Optional.of(availableBook));
        when(borrowRepository.existsByBookIdAndBorrowerEmailIgnoreCaseAndReturnDateIsNull(10L, "an.nguyen@example.com"))
            .thenReturn(true);

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> borrowService.borrowBook(formDto)
        );

        assertEquals("borrowerEmail", exception.getFieldName());
        assertTrue(exception.getMessage().contains("chưa trả"));
        assertEquals(3, availableBook.getQuantity(), "Số lượng sách không được thay đổi khi mượn thất bại");
        verify(borrowRepository, never()).save(any(Borrow.class));
    }

    @Test
    @DisplayName("Unit Test 4: Mượn sách thất bại khi hạn trả nhỏ hơn ngày mượn")
    void borrowBook_ShouldThrowBusinessException_WhenDueDateIsBeforeBorrowDate() {
        LocalDate today = LocalDate.now();
        BorrowFormDto formDto = new BorrowFormDto(
            10L,
            "Lê Hoàng Nam",
            "nam.le@example.com",
            today,
            today.minusDays(1)
        );

        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> borrowService.borrowBook(formDto)
        );

        assertEquals("dueDate", exception.getFieldName());
        verify(borrowRepository, never()).save(any(Borrow.class));
    }

    @Test
    @DisplayName("Unit Test 5: Trả sách thành công cập nhật ngày trả và cộng lại 1 cuốn vào kho")
    void returnBook_ShouldSetReturnDateAndIncreaseBookQuantity_WhenBorrowIsActive() {
        Borrow activeBorrow = new Borrow(
            availableBook,
            "Nguyễn Văn An",
            "an.nguyen@example.com",
            LocalDate.now().minusDays(5),
            LocalDate.now().plusDays(9)
        );
        activeBorrow.setId(55L);

        when(borrowRepository.findWithBookById(55L)).thenReturn(Optional.of(activeBorrow));
        when(borrowRepository.save(activeBorrow)).thenReturn(activeBorrow);

        Borrow returned = borrowService.returnBook(55L);

        assertTrue(returned.isReturned());
        assertEquals(LocalDate.now(), returned.getReturnDate());
        assertEquals(4, availableBook.getQuantity(), "Số lượng sách phải tăng từ 3 lên 4 sau khi trả");
        verify(bookRepository).save(availableBook);
        verify(borrowRepository).save(activeBorrow);
    }

    @Test
    @DisplayName("Unit Test 6: Trả sách thất bại khi phiếu mượn đã được trả trước đó")
    void returnBook_ShouldThrowBusinessException_WhenBorrowIsAlreadyReturned() {
        Borrow alreadyReturnedBorrow = new Borrow(
            availableBook,
            "Nguyễn Văn An",
            "an.nguyen@example.com",
            LocalDate.now().minusDays(15),
            LocalDate.now().minusDays(5)
        );
        alreadyReturnedBorrow.setId(56L);
        alreadyReturnedBorrow.setReturnDate(LocalDate.now().minusDays(6));

        when(borrowRepository.findWithBookById(56L)).thenReturn(Optional.of(alreadyReturnedBorrow));

        assertThrows(BusinessException.class, () -> borrowService.returnBook(56L));
        assertEquals(3, availableBook.getQuantity(), "Số lượng sách không được cộng thêm lần nữa");
        verify(bookRepository, never()).save(any(Book.class));
    }
}

