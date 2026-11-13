-- =============================================================================
-- Database Schema: Hệ thống Quản lý Sách & Mượn Trả Sách (Book Store / Library)
-- DBMS: MySQL 8.0+
-- =============================================================================

CREATE DATABASE IF NOT EXISTS book_store_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE book_store_db;

-- 1. Bảng categories (Thể loại sách)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_categories_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Bảng books (Sách)
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    publish_year INT NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    category_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uk_books_code UNIQUE (code),
    CONSTRAINT chk_books_quantity CHECK (quantity >= 0),
    CONSTRAINT chk_books_publish_year CHECK (publish_year >= 1000),
    CONSTRAINT fk_books_category FOREIGN KEY (category_id)
        REFERENCES categories (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    INDEX idx_books_category_id (category_id),
    INDEX idx_books_title (title),
    INDEX idx_books_author (author),
    INDEX idx_books_publish_year (publish_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Bảng borrows (Phiếu mượn sách)
CREATE TABLE IF NOT EXISTS borrows (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    book_id BIGINT NOT NULL,
    borrower_name VARCHAR(150) NOT NULL,
    borrower_email VARCHAR(150) NOT NULL,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_borrows_dates CHECK (due_date >= borrow_date),
    CONSTRAINT fk_borrows_book FOREIGN KEY (book_id)
        REFERENCES books (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    INDEX idx_borrows_book_email_return (book_id, borrower_email, return_date),
    INDEX idx_borrows_return_due (return_date, due_date),
    INDEX idx_borrows_borrower_email (borrower_email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

