-- =============================================================================
-- Sample Data: Dữ liệu mẫu kiểm thử đầy đủ các kịch bản (còn sách, hết sách,
-- đang mượn trong hạn, đang mượn quá hạn, đã trả)
-- =============================================================================

USE book_store_db;

-- 1. Thể loại sách
INSERT INTO categories (id, name, created_at, updated_at) VALUES
(1, 'Công nghệ thông tin', NOW(), NOW()),
(2, 'Văn học Việt Nam', NOW(), NOW()),
(3, 'Kinh tế & Quản trị', NOW(), NOW()),
(4, 'Khoa học & Tâm lý', NOW(), NOW()),
(5, 'Kỹ năng sống', NOW(), NOW());

-- 2. Danh sách sách (12 đầu sách để kiểm thử phân trang, tìm kiếm, lọc, hết sách)
INSERT INTO books (id, code, title, author, publish_year, quantity, category_id, created_at, updated_at) VALUES
(1, 'BK-CNTT-001', 'Clean Code - Mã nguồn sạch', 'Robert C. Martin', 2008, 5, 1, NOW(), NOW()),
(2, 'BK-CNTT-002', 'Effective Java (3rd Edition)', 'Joshua Bloch', 2018, 3, 1, NOW(), NOW()),
(3, 'BK-CNTT-003', 'Spring in Action (6th Edition)', 'Craig Walls', 2022, 0, 1, NOW(), NOW()),
(4, 'BK-CNTT-004', 'Designing Data-Intensive Applications', 'Martin Kleppmann', 2017, 4, 1, NOW(), NOW()),
(5, 'BK-VHVN-001', 'Dế Mèn Phiêu Lưu Ký', 'Tô Hoài', 1941, 8, 2, NOW(), NOW()),
(6, 'BK-VHVN-002', 'Mắt Biếc', 'Nguyễn Nhật Ánh', 1990, 6, 2, NOW(), NOW()),
(7, 'BK-VHVN-003', 'Số Đỏ', 'Vũ Trọng Phụng', 1936, 2, 2, NOW(), NOW()),
(8, 'BK-KTQT-001', 'Từ Tốt Đến Vĩ Đại (Good to Great)', 'Jim Collins', 2001, 4, 3, NOW(), NOW()),
(9, 'BK-KTQT-002', 'Khởi Nghiệp Tinh Gọn (The Lean Startup)', 'Eric Ries', 2011, 0, 3, NOW(), NOW()),
(10, 'BK-KHTL-001', 'Tư Duy Nhanh Và Chậm', 'Daniel Kahneman', 2011, 5, 4, NOW(), NOW()),
(11, 'BK-KHTL-002', 'Lược Sử Thời Gian', 'Stephen Hawking', 1988, 3, 4, NOW(), NOW()),
(12, 'BK-KNS-001', 'Đắc Nhân Tâm', 'Dale Carnegie', 1936, 10, 5, NOW(), NOW());

-- 3. Danh sách phiếu mượn (đang mượn trong hạn, đang mượn QUÁ HẠN, và đã trả)
INSERT INTO borrows (id, book_id, borrower_name, borrower_email, borrow_date, due_date, return_date, created_at, updated_at) VALUES
-- Đang mượn và QUÁ HẠN (return_date IS NULL, due_date < CURDATE())
(1, 1, 'Nguyễn Văn An', 'an.nguyen@example.com', DATE_SUB(CURDATE(), INTERVAL 20 DAY), DATE_SUB(CURDATE(), INTERVAL 6 DAY), NULL, NOW(), NOW()),
(2, 3, 'Trần Thị Bích', 'bich.tran@example.com', DATE_SUB(CURDATE(), INTERVAL 18 DAY), DATE_SUB(CURDATE(), INTERVAL 4 DAY), NULL, NOW(), NOW()),

-- Đang mượn và CÒN HẠN (return_date IS NULL, due_date >= CURDATE())
(3, 2, 'Lê Hoàng Nam', 'nam.le@example.com', DATE_SUB(CURDATE(), INTERVAL 5 DAY), DATE_ADD(CURDATE(), INTERVAL 9 DAY), NULL, NOW(), NOW()),
(4, 6, 'Phạm Minh Châu', 'chau.pham@example.com', DATE_SUB(CURDATE(), INTERVAL 2 DAY), DATE_ADD(CURDATE(), INTERVAL 12 DAY), NULL, NOW(), NOW()),
(5, 10, 'Nguyễn Văn An', 'an.nguyen@example.com', DATE_SUB(CURDATE(), INTERVAL 1 DAY), DATE_ADD(CURDATE(), INTERVAL 13 DAY), NULL, NOW(), NOW()),

-- Đã trả (return_date IS NOT NULL)
(6, 5, 'Hoàng Đức Thịnh', 'thinh.hoang@example.com', DATE_SUB(CURDATE(), INTERVAL 25 DAY), DATE_SUB(CURDATE(), INTERVAL 11 DAY), DATE_SUB(CURDATE(), INTERVAL 14 DAY), NOW(), NOW()),
(7, 1, 'Lê Hoàng Nam', 'nam.le@example.com', DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 16 DAY), DATE_SUB(CURDATE(), INTERVAL 18 DAY), NOW(), NOW());

