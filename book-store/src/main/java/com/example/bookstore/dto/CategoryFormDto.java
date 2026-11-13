package com.example.bookstore.dto;

import com.example.bookstore.entity.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryFormDto {

    private Long id;

    @NotBlank(message = "Tên thể loại không được để trống")
    @Size(min = 2, max = 100, message = "Tên thể loại phải từ 2 đến 100 ký tự")
    private String name;

    public CategoryFormDto() {
    }

    public CategoryFormDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static CategoryFormDto fromEntity(Category category) {
        return new CategoryFormDto(category.getId(), category.getName());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }
}

