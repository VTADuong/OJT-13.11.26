package com.example.bookstore.service.impl;

import com.example.bookstore.dto.CategoryFormDto;
import com.example.bookstore.dto.CategorySummaryDto;
import com.example.bookstore.entity.Category;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.exception.DuplicateResourceException;
import com.example.bookstore.exception.ResourceNotFoundException;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CategoryRepository;
import com.example.bookstore.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, BookRepository bookRepository) {
        this.categoryRepository = categoryRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    @Override
    public List<CategorySummaryDto> findAllWithBookCount() {
        return categoryRepository.findAllWithBookCount();
    }

    @Override
    public Category findById(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("thể loại sách", id));
    }

    @Override
    @Transactional
    public Category createCategory(CategoryFormDto formDto) {
        String normalizedName = formDto.getName() != null ? formDto.getName().trim() : "";
        if (categoryRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new DuplicateResourceException("name", "Tên thể loại \"" + normalizedName + "\" đã tồn tại.");
        }
        Category category = new Category(normalizedName);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, CategoryFormDto formDto) {
        Category existing = findById(id);
        String normalizedName = formDto.getName() != null ? formDto.getName().trim() : "";
        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new DuplicateResourceException("name", "Tên thể loại \"" + normalizedName + "\" đã tồn tại.");
        }
        existing.setName(normalizedName);
        return categoryRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category existing = findById(id);
        if (bookRepository.existsByCategoryId(id)) {
            throw new BusinessException("Không thể xoá thể loại \"" + existing.getName()
                + "\" vì đang có sách thuộc thể loại này.");
        }
        categoryRepository.delete(existing);
    }
}

