package com.example.bookstore.repository;

import com.example.bookstore.dto.CategorySummaryDto;
import com.example.bookstore.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<Category> findByNameIgnoreCase(String name);

    List<Category> findAllByOrderByNameAsc();

    @Query("""
        SELECT new com.example.bookstore.dto.CategorySummaryDto(
            c.id,
            c.name,
            COUNT(b.id),
            c.createdAt
        )
        FROM Category c
        LEFT JOIN c.books b
        GROUP BY c.id, c.name, c.createdAt
        ORDER BY c.name ASC
        """)
    List<CategorySummaryDto> findAllWithBookCount();
}

