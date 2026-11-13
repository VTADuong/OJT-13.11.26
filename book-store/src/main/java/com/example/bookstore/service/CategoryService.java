package com.example.bookstore.service;

import com.example.bookstore.dto.CategoryFormDto;
import com.example.bookstore.dto.CategorySummaryDto;
import com.example.bookstore.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAll();

    List<CategorySummaryDto> findAllWithBookCount();

    Category findById(Long id);

    Category createCategory(CategoryFormDto formDto);

    Category updateCategory(Long id, CategoryFormDto formDto);

    void deleteCategory(Long id);
}

