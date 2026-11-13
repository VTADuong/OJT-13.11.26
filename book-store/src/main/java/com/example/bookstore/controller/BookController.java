package com.example.bookstore.controller;

import com.example.bookstore.dto.BookFormDto;
import com.example.bookstore.dto.BookSearchCriteria;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.BorrowService;
import com.example.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final BorrowService borrowService;

    public BookController(BookService bookService,
                          CategoryService categoryService,
                          BorrowService borrowService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.borrowService = borrowService;
    }

    @GetMapping
    public String listBooks(@ModelAttribute("criteria") BookSearchCriteria criteria, Model model) {
        Page<Book> bookPage = bookService.searchBooks(criteria);
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("activeBorrowCount", borrowService.countActiveBorrows());
        model.addAttribute("overdueBorrowCount", borrowService.countOverdueBorrows());
        model.addAttribute("activeMenu", "books");
        return "books/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("bookForm", new BookFormDto());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", "books");
        return "books/form";
    }

    @PostMapping
    public String createBook(@Valid @ModelAttribute("bookForm") BookFormDto bookForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "books");
            return "books/form";
        }

        try {
            Book created = bookService.createBook(bookForm);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã thêm sách \"" + created.getTitle() + "\" (" + created.getCode() + ") thành công!"
            );
            return "redirect:/books";
        } catch (BusinessException ex) {
            if (ex.getFieldName() != null) {
                bindingResult.rejectValue(ex.getFieldName(), "error.bookForm", ex.getMessage());
            } else {
                model.addAttribute("errorMessage", ex.getMessage());
            }
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "books");
            return "books/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        Book book = bookService.findById(id);
        model.addAttribute("bookForm", BookFormDto.fromEntity(book));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", "books");
        return "books/form";
    }

    @PostMapping("/{id}")
    public String updateBook(@PathVariable Long id,
                             @Valid @ModelAttribute("bookForm") BookFormDto bookForm,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        bookForm.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "books");
            return "books/form";
        }

        try {
            Book updated = bookService.updateBook(id, bookForm);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã cập nhật sách \"" + updated.getTitle() + "\" (" + updated.getCode() + ") thành công!"
            );
            return "redirect:/books";
        } catch (BusinessException ex) {
            if (ex.getFieldName() != null) {
                bindingResult.rejectValue(ex.getFieldName(), "error.bookForm", ex.getMessage());
            } else {
                model.addAttribute("errorMessage", ex.getMessage());
            }
            model.addAttribute("categories", categoryService.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "books");
            return "books/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Book book = bookService.findById(id);
            String title = book.getTitle();
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã xoá sách \"" + title + "\" khỏi hệ thống thành công!"
            );
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/books";
    }
}

