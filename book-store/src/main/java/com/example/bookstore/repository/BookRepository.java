package com.example.bookstore.repository;

import com.example.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByCodeIgnoreCaseAndIdNot(String code, Long id);

    boolean existsByCategoryId(Long categoryId);

    long countByCategoryId(Long categoryId);

    @EntityGraph(attributePaths = {"category"})
    Optional<Book> findWithCategoryById(Long id);

    @EntityGraph(attributePaths = {"category"})
    List<Book> findAllByOrderByTitleAsc();

    @EntityGraph(attributePaths = {"category"})
    @Query("""
        SELECT b FROM Book b
        WHERE (:keyword IS NULL OR :keyword = ''
               OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(b.code) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR b.category.id = :categoryId)
        """)
    Page<Book> searchBooks(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           Pageable pageable);
}

