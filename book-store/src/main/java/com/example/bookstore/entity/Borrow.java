package com.example.bookstore.entity;

import com.example.bookstore.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(
    name = "borrows",
    indexes = {
        @Index(name = "idx_borrows_book_email_return", columnList = "book_id, borrower_email, return_date"),
        @Index(name = "idx_borrows_return_due", columnList = "return_date, due_date"),
        @Index(name = "idx_borrows_borrower_email", columnList = "borrower_email")
    }
)
public class Borrow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "book_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_borrows_book")
    )
    private Book book;

    @Column(name = "borrower_name", nullable = false, length = 150)
    private String borrowerName;

    @Column(name = "borrower_email", nullable = false, length = 150)
    private String borrowerEmail;

    @Column(name = "borrow_date", nullable = false)
    private LocalDate borrowDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    public Borrow() {
    }

    public Borrow(Book book, String borrowerName, String borrowerEmail, LocalDate borrowDate, LocalDate dueDate) {
        this.book = book;
        this.borrowerName = borrowerName != null ? borrowerName.trim() : null;
        this.borrowerEmail = borrowerEmail != null ? borrowerEmail.trim().toLowerCase() : null;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    /**
     * Kiểm tra phiếu mượn đã được trả hay chưa.
     */
    public boolean isReturned() {
        return this.returnDate != null;
    }

    /**
     * Kiểm tra phiếu mượn đang mượn và đã quá hạn tính đến ngày hiện tại hay chưa.
     */
    public boolean isOverdue() {
        return isOverdue(LocalDate.now());
    }

    /**
     * Kiểm tra phiếu mượn đang mượn và quá hạn tại một mốc ngày cụ thể.
     */
    public boolean isOverdue(LocalDate referenceDate) {
        return !isReturned() && this.dueDate != null && referenceDate.isAfter(this.dueDate);
    }

    /**
     * Kiểm tra phiếu mượn đã trả nhưng trả muộn so với hạn trả.
     */
    public boolean isReturnedLate() {
        return isReturned() && this.dueDate != null && this.returnDate.isAfter(this.dueDate);
    }

    /**
     * Số ngày quá hạn (nếu đang quá hạn hoặc trả trễ).
     */
    public long getOverdueDays() {
        if (isOverdue()) {
            return ChronoUnit.DAYS.between(this.dueDate, LocalDate.now());
        }
        if (isReturnedLate()) {
            return ChronoUnit.DAYS.between(this.dueDate, this.returnDate);
        }
        return 0;
    }

    /**
     * Số ngày còn lại trước hạn trả (khi đang mượn và chưa quá hạn).
     */
    public long getRemainingDays() {
        if (!isReturned() && !isOverdue() && this.dueDate != null) {
            return ChronoUnit.DAYS.between(LocalDate.now(), this.dueDate);
        }
        return 0;
    }

    /**
     * Thực hiện nghiệp vụ trả sách: cập nhật ngày trả và hoàn lại số lượng sách vào kho.
     */
    public void markReturned(LocalDate actualReturnDate) {
        if (isReturned()) {
            throw new BusinessException("Phiếu mượn này đã được trả vào ngày " + this.returnDate + ".");
        }
        this.returnDate = actualReturnDate != null ? actualReturnDate : LocalDate.now();
        if (this.book != null) {
            this.book.returnOne();
        }
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName != null ? borrowerName.trim() : null;
    }

    public String getBorrowerEmail() {
        return borrowerEmail;
    }

    public void setBorrowerEmail(String borrowerEmail) {
        this.borrowerEmail = borrowerEmail != null ? borrowerEmail.trim().toLowerCase() : null;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(LocalDate borrowDate) {
        this.borrowDate = borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}

