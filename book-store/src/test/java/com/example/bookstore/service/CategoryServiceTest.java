package com.example.bookstore.service;

import com.example.bookstore.dto.CategoryFormDto;
import com.example.bookstore.entity.Category;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    @DisplayName("Unit Test 11: Thêm thể loại sách thành công khi tên không trùng")
    void createCategory_ShouldSucceed_WhenNameIsUnique() {
        CategoryFormDto formDto = new CategoryFormDto(null, "Khoa học dữ liệu");
        when(categoryRepository.existsByNameIgnoreCase("Khoa học dữ liệu")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setId(5L);
            return c;
        });

        Category saved = categoryService.createCategory(formDto);

        assertNotNull(saved);
        assertEquals(5L, saved.getId());
        assertEquals("Khoa học dữ liệu", saved.getName());
    }

    @Test
    @DisplayName("Unit Test 12: Thêm thể loại thất bại khi tên thể loại bị trùng")
    void createCategory_ShouldThrowDuplicateResourceException_WhenNameAlreadyExists() {
        CategoryFormDto formDto = new CategoryFormDto(null, "Công nghệ thông tin");
        when(categoryRepository.existsByNameIgnoreCase("Công nghệ thông tin")).thenReturn(true);

        DuplicateResourceException ex = assertThrows(
            DuplicateResourceException.class,
            () -> categoryService.createCategory(formDto)
        );

        assertEquals("name", ex.getFieldName());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Unit Test 13: Không cho phép xoá thể loại khi đang có sách thuộc thể loại đó")
    void deleteCategory_ShouldThrowBusinessException_WhenCategoryHasBooks() {
        Category existing = new Category(1L, "Công nghệ thông tin");
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(bookRepository.existsByCategoryId(1L)).thenReturn(true);

        assertThrows(BusinessException.class, () -> categoryService.deleteCategory(1L));
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}

