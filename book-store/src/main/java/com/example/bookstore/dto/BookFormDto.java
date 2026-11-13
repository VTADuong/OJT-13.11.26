package com.example.bookstore.dto;

import com.example.bookstore.entity.Book;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class BookFormDto {

    private Long id;

    @NotBlank(message = "Mã sách không được để trống")
    @Size(min = 2, max = 50, message = "Mã sách phải từ 2 đến 50 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9-_]+$", message = "Mã sách chỉ gồm chữ cái, chữ số, dấu gạch ngang (-) hoặc gạch dưới (_)")
    private String code;

    @NotBlank(message = "Tên sách không được để trống")
    @Size(min = 2, max = 200, message = "Tên sách phải từ 2 đến 200 ký tự")
    private String title;

    @NotBlank(message = "Tên tác giả không được để trống")
    @Size(min = 2, max = 150, message = "Tên tác giả phải từ 2 đến 150 ký tự")
    private String author;

    @NotNull(message = "Năm xuất bản không được để trống")
    @Min(value = 1000, message = "Năm xuất bản phải từ năm 1000 trở lên")
    @Max(value = 2030, message = "Năm xuất bản không được vượt quá năm 2030")
    private Integer publishYear;

    @NotNull(message = "Số lượng sách không được để trống")
    @Min(value = 0, message = "Số lượng sách phải lớn hơn hoặc bằng 0")
    @Max(value = 100000, message = "Số lượng sách không vượt quá 100.000")
    private Integer quantity;

    @NotNull(message = "Vui lòng chọn thể loại sách")
    private Long categoryId;

    public BookFormDto() {
    }

    public BookFormDto(Long id, String code, String title, String author, Integer publishYear, Integer quantity, Long categoryId) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.categoryId = categoryId;
    }

    public static BookFormDto fromEntity(Book book) {
        return new BookFormDto(
            book.getId(),
            book.getCode(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublishYear(),
            book.getQuantity(),
            book.getCategory() != null ? book.getCategory().getId() : null
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code != null ? code.trim().toUpperCase() : null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title != null ? title.trim() : null;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author != null ? author.trim() : null;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}

