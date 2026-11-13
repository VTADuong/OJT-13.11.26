package com.example.bookstore.controller;

import com.example.bookstore.dto.CategoryFormDto;
import com.example.bookstore.entity.Category;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.findAllWithBookCount());
        model.addAttribute("activeMenu", "categories");
        return "categories/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("categoryForm", new CategoryFormDto());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", "categories");
        return "categories/form";
    }

    @PostMapping
    public String createCategory(@Valid @ModelAttribute("categoryForm") CategoryFormDto categoryForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "categories");
            return "categories/form";
        }

        try {
            Category created = categoryService.createCategory(categoryForm);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã thêm thể loại \"" + created.getName() + "\" thành công!"
            );
            return "redirect:/categories";
        } catch (BusinessException ex) {
            if (ex.getFieldName() != null) {
                bindingResult.rejectValue(ex.getFieldName(), "error.categoryForm", ex.getMessage());
            } else {
                model.addAttribute("errorMessage", ex.getMessage());
            }
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "categories");
            return "categories/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Category category = categoryService.findById(id);
        model.addAttribute("categoryForm", CategoryFormDto.fromEntity(category));
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", "categories");
        return "categories/form";
    }

    @PostMapping("/{id}")
    public String updateCategory(@PathVariable Long id,
                                 @Valid @ModelAttribute("categoryForm") CategoryFormDto categoryForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        categoryForm.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "categories");
            return "categories/form";
        }

        try {
            Category updated = categoryService.updateCategory(id, categoryForm);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã cập nhật thể loại thành \"" + updated.getName() + "\" thành công!"
            );
            return "redirect:/categories";
        } catch (BusinessException ex) {
            if (ex.getFieldName() != null) {
                bindingResult.rejectValue(ex.getFieldName(), "error.categoryForm", ex.getMessage());
            } else {
                model.addAttribute("errorMessage", ex.getMessage());
            }
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "categories");
            return "categories/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.findById(id);
            String name = category.getName();
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã xoá thể loại \"" + name + "\" thành công!"
            );
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/categories";
    }
}

