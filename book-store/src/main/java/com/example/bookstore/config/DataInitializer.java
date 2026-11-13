package com.example.bookstore.config;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Borrow;
import com.example.bookstore.entity.Category;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.BorrowRepository;
import com.example.bookstore.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final BorrowRepository borrowRepository;

    public DataInitializer(CategoryRepository categoryRepository,
                           BookRepository bookRepository,
                           BorrowRepository borrowRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
        this.borrowRepository = borrowRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            return;
        }

        Category catIT = categoryRepository.save(new Category("Công nghệ thông tin"));
        Category catLit = categoryRepository.save(new Category("Văn học Việt Nam"));
        Category catBiz = categoryRepository.save(new Category("Kinh tế & Quản trị"));
        Category catSci = categoryRepository.save(new Category("Khoa học & Tâm lý"));
        Category catLife = categoryRepository.save(new Category("Kỹ năng sống"));

        Book b1 = new Book("BK-CNTT-001", "Clean Code - Mã nguồn sạch", "Robert C. Martin", 2008, 5, catIT);
        Book b2 = new Book("BK-CNTT-002", "Effective Java (3rd Edition)", "Joshua Bloch", 2018, 3, catIT);
        Book b3 = new Book("BK-CNTT-003", "Spring in Action (6th Edition)", "Craig Walls", 2022, 0, catIT);
        Book b4 = new Book("BK-CNTT-004", "Designing Data-Intensive Applications", "Martin Kleppmann", 2017, 4, catIT);
        Book b5 = new Book("BK-VHVN-001", "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", 1941, 8, catLit);
        Book b6 = new Book("BK-VHVN-002", "Mắt Biếc", "Nguyễn Nhật Ánh", 1990, 6, catLit);
        Book b7 = new Book("BK-VHVN-003", "Số Đỏ", "Vũ Trọng Phụng", 1936, 2, catLit);
        Book b8 = new Book("BK-KTQT-001", "Từ Tốt Đến Vĩ Đại (Good to Great)", "Jim Collins", 2001, 4, catBiz);
        Book b9 = new Book("BK-KTQT-002", "Khởi Nghiệp Tinh Gọn (The Lean Startup)", "Eric Ries", 2011, 0, catBiz);
        Book b10 = new Book("BK-KHTL-001", "Tư Duy Nhanh Và Chậm", "Daniel Kahneman", 2011, 5, catSci);
        Book b11 = new Book("BK-KHTL-002", "Lược Sử Thời Gian", "Stephen Hawking", 1988, 3, catSci);
        Book b12 = new Book("BK-KNS-001", "Đắc Nhân Tâm", "Dale Carnegie", 1936, 10, catLife);

        bookRepository.saveAll(List.of(b1, b2, b3, b4, b5, b6, b7, b8, b9, b10, b11, b12));

        LocalDate today = LocalDate.now();

        // 2 phiếu đang mượn QUÁ HẠN (returnDate == null, dueDate < today)
        Borrow overdue1 = new Borrow(b1, "Nguyễn Văn An", "an.nguyen@example.com", today.minusDays(20), today.minusDays(6));
        Borrow overdue2 = new Borrow(b3, "Trần Thị Bích", "bich.tran@example.com", today.minusDays(18), today.minusDays(4));

        // 3 phiếu đang mượn CÒN HẠN (returnDate == null, dueDate >= today)
        Borrow active1 = new Borrow(b2, "Lê Hoàng Nam", "nam.le@example.com", today.minusDays(5), today.plusDays(9));
        Borrow active2 = new Borrow(b6, "Phạm Minh Châu", "chau.pham@example.com", today.minusDays(2), today.plusDays(12));
        Borrow active3 = new Borrow(b10, "Nguyễn Văn An", "an.nguyen@example.com", today.minusDays(1), today.plusDays(13));

        // 2 phiếu ĐÃ TRẢ (returnDate != null)
        Borrow returned1 = new Borrow(b5, "Hoàng Đức Thịnh", "thinh.hoang@example.com", today.minusDays(25), today.minusDays(11));
        returned1.setReturnDate(today.minusDays(14));

        Borrow returned2 = new Borrow(b1, "Lê Hoàng Nam", "nam.le@example.com", today.minusDays(30), today.minusDays(16));
        returned2.setReturnDate(today.minusDays(18));

        borrowRepository.saveAll(List.of(overdue1, overdue2, active1, active2, active3, returned1, returned2));
    }
}

