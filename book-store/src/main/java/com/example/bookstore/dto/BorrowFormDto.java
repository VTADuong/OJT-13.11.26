package com.example.bookstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class BorrowFormDto {

    @NotNull(message = "Vui lòng chọn sách cần mượn")
    private Long bookId;

    @NotBlank(message = "Tên người mượn không được để trống")
    @Size(min = 2, max = 150, message = "Tên người mượn phải từ 2 đến 150 ký tự")
    private String borrowerName;

    @NotBlank(message = "Email người mượn không được để trống")
    @Email(message = "Địa chỉ email không đúng định dạng (ví dụ: ten@example.com)")
    @Size(max = 150, message = "Email tối đa 150 ký tự")
    private String borrowerEmail;

    @NotNull(message = "Ngày mượn không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate borrowDate = LocalDate.now();

    @NotNull(message = "Hạn trả không được để trống")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate = LocalDate.now().plusDays(14);

    public BorrowFormDto() {
    }

    public BorrowFormDto(Long bookId, String borrowerName, String borrowerEmail, LocalDate borrowDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.borrowerName = borrowerName;
        this.borrowerEmail = borrowerEmail;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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
}

