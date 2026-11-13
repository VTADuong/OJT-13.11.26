package com.example.bookstore.service.impl;

import com.example.bookstore.dto.BorrowFormDto;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Borrow;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.BorrowRepository;
import com.example.bookstore.service.BorrowService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class BorrowServiceImpl implements BorrowService {

    private static final Set<String> VALID_STATUSES = Set.of("ACTIVE", "OVERDUE", "RETURNED", "ALL");

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;

    public BorrowServiceImpl(BorrowRepository borrowRepository, BookRepository bookRepository) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Borrow> searchBorrows(String keyword, String status) {
        String normalizedKeyword = keyword != null ? keyword.trim() : "";
        String normalizedStatus = (status != null && VALID_STATUSES.contains(status.toUpperCase()))
            ? status.toUpperCase()
            : "ACTIVE";
        return borrowRepository.searchBorrows(normalizedKeyword, normalizedStatus, LocalDate.now());
    }

    @Override
    public Borrow findById(Long id) {
        return borrowRepository.findWithBookById(id)
            .orElseThrow(() -> new ResourceNotFoundException("phiếu mượn", id));
    }

    @Override
    @Transactional
    public Borrow borrowBook(BorrowFormDto formDto) {
        LocalDate borrowDate = formDto.getBorrowDate() != null ? formDto.getBorrowDate() : LocalDate.now();
        LocalDate dueDate = formDto.getDueDate();

        if (dueDate == null || dueDate.isBefore(borrowDate)) {
            throw new BusinessException("dueDate", "Hạn trả phải lớn hơn hoặc bằng ngày mượn.");
        }

        Book book = bookRepository.findWithCategoryById(formDto.getBookId())
            .orElseThrow(() -> new ResourceNotFoundException("sách", formDto.getBookId()));

        // Quy tắc 1: Hết sách thì không cho mượn
        if (!book.isAvailable()) {
            throw new BusinessException("bookId", "Sách \"" + book.getTitle() + "\" đã hết trong kho, không thể mượn.");
        }

        String normalizedEmail = formDto.getBorrowerEmail() != null
            ? formDto.getBorrowerEmail().trim().toLowerCase()
            : "";

        // Quy tắc 2: Một email không mượn trùng cùng một cuốn khi chưa trả
        if (borrowRepository.existsByBookIdAndBorrowerEmailIgnoreCaseAndReturnDateIsNull(book.getId(), normalizedEmail)) {
            throw new DuplicateResourceException(
                "borrowerEmail",
                "Email \"" + normalizedEmail + "\" đang mượn cuốn sách \"" + book.getTitle() + "\" và chưa trả."
            );
        }

        // Giảm số lượng tồn kho 1 đơn vị (OOP domain method)
        book.borrowOne();
        bookRepository.save(book);

        Borrow borrow = new Borrow(
            book,
            formDto.getBorrowerName().trim(),
            normalizedEmail,
            borrowDate,
            dueDate
        );

        return borrowRepository.save(borrow);
    }

    @Override
    @Transactional
    public Borrow returnBook(Long borrowId) {
        Borrow borrow = findById(borrowId);

        // Thực hiện trả sách qua domain method (cập nhật ngày trả + tăng số lượng sách lên 1)
        borrow.markReturned(LocalDate.now());

        bookRepository.save(borrow.getBook());
        return borrowRepository.save(borrow);
    }

    @Override
    public long countActiveBorrows() {
        return borrowRepository.countByReturnDateIsNull();
    }

    @Override
    public long countOverdueBorrows() {
        return borrowRepository.countByReturnDateIsNullAndDueDateBefore(LocalDate.now());
    }

    @Override
    public long countReturnedBorrows() {
        return borrowRepository.countByReturnDateIsNotNull();
    }
}

