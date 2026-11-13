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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "books",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_books_code", columnNames = "code")
    },
    indexes = {
        @Index(name = "idx_books_category_id", columnList = "category_id"),
        @Index(name = "idx_books_title", columnList = "title"),
        @Index(name = "idx_books_author", columnList = "author"),
        @Index(name = "idx_books_publish_year", columnList = "publish_year")
    }
)
public class Book extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "author", nullable = false, length = 150)
    private String author;

    @Column(name = "publish_year", nullable = false)
    private Integer publishYear;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "category_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_books_category")
    )
    private Category category;

    public Book() {
    }

    public Book(String code, String title, String author, Integer publishYear, Integer quantity, Category category) {
        this.code = code;
        this.title = title;
        this.author = author;
        this.publishYear = publishYear;
        this.quantity = quantity;
        this.category = category;
    }

    /**
     * Kiểm tra sách còn trong kho để cho mượn hay không.
     */
    public boolean isAvailable() {
        return this.quantity != null && this.quantity > 0;
    }

    /**
     * Thực hiện nghiệp vụ giảm số lượng tồn kho khi mượn 1 cuốn sách.
     */
    public void borrowOne() {
        if (!isAvailable()) {
            throw new BusinessException("bookId", "Sách \"" + this.title + "\" đã hết trong kho, không thể mượn.");
        }
        this.quantity = this.quantity - 1;
    }

    /**
     * Thực hiện nghiệp vụ tăng số lượng tồn kho khi trả 1 cuốn sách.
     */
    public void returnOne() {
        this.quantity = (this.quantity == null ? 0 : this.quantity) + 1;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}

