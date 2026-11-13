package com.example.bookstore.repository;

import com.example.bookstore.entity.Borrow;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    /**
     * Kiểm tra một email có đang mượn trùng cùng một cuốn sách mà chưa trả (returnDate IS NULL) hay không.
     */
    boolean existsByBookIdAndBorrowerEmailIgnoreCaseAndReturnDateIsNull(Long bookId, String borrowerEmail);

    boolean existsByBookIdAndReturnDateIsNull(Long bookId);

    boolean existsByBookId(Long bookId);

    long countByReturnDateIsNull();

    long countByReturnDateIsNullAndDueDateBefore(LocalDate date);

    long countByReturnDateIsNotNull();

    @EntityGraph(attributePaths = {"book", "book.category"})
    Optional<Borrow> findWithBookById(Long id);

    /**
     * Truy vấn danh sách phiếu mượn kèm thông tin sách & thể loại (tránh lỗi N+1 query),
     * hỗ trợ lọc theo trạng thái (ACTIVE, OVERDUE, RETURNED, ALL) và tìm kiếm theo từ khoá.
     */
    @EntityGraph(attributePaths = {"book", "book.category"})
    @Query("""
        SELECT br FROM Borrow br
        JOIN br.book b
        WHERE (:keyword IS NULL OR :keyword = ''
               OR LOWER(br.borrowerName) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(br.borrowerEmail) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (
               :status = 'ALL'
               OR (:status = 'ACTIVE' AND br.returnDate IS NULL)
               OR (:status = 'OVERDUE' AND br.returnDate IS NULL AND br.dueDate < :today)
               OR (:status = 'RETURNED' AND br.returnDate IS NOT NULL)
          )
        ORDER BY
          CASE WHEN br.returnDate IS NULL AND br.dueDate < :today THEN 0
               WHEN br.returnDate IS NULL THEN 1
               ELSE 2 END ASC,
          br.dueDate ASC,
          br.id DESC
        """)
    List<Borrow> searchBorrows(@Param("keyword") String keyword,
                               @Param("status") String status,
                               @Param("today") LocalDate today);
}

