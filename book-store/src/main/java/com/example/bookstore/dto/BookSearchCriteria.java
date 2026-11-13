package com.example.bookstore.dto;

public class BookSearchCriteria {

    private String keyword = "";
    private Long categoryId;
    private int page = 0;
    private int size = 6;
    private String sortBy = "title";
    private String sortDir = "asc";

    public BookSearchCriteria() {
    }

    public BookSearchCriteria(String keyword, Long categoryId, int page, int size, String sortBy, String sortDir) {
        this.keyword = keyword != null ? keyword.trim() : "";
        this.categoryId = categoryId;
        this.page = Math.max(page, 0);
        this.size = size > 0 && size <= 50 ? size : 6;
        this.sortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "title";
        this.sortDir = ("desc".equalsIgnoreCase(sortDir)) ? "desc" : "asc";
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword != null ? keyword.trim() : "";
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(page, 0);
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = (size > 0 && size <= 50) ? size : 6;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = (sortBy != null && !sortBy.isBlank()) ? sortBy : "title";
    }

    public String getSortDir() {
        return sortDir;
    }

    public void setSortDir(String sortDir) {
        this.sortDir = ("desc".equalsIgnoreCase(sortDir)) ? "desc" : "asc";
    }
}

