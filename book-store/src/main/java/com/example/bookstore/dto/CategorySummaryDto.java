package com.example.bookstore.dto;

import java.time.LocalDateTime;

public class CategorySummaryDto {

    private final Long id;
    private final String name;
    private final long bookCount;
    private final LocalDateTime createdAt;

    public CategorySummaryDto(Long id, String name, long bookCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.bookCount = bookCount;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getBookCount() {
        return bookCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

