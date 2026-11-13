package com.example.bookstore.controller;

import com.example.bookstore.dto.BorrowFormDto;
import com.example.bookstore.entity.Borrow;
import com.example.bookstore.exception.BusinessException;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/borrows")
public class BorrowController {

    private final BorrowService borrowService;
    private final BookService bookService;

    public BorrowController(BorrowService borrowService, BookService bookService) {
        this.borrowService = borrowService;
        this.bookService = bookService;
    }

    @GetMapping
    public String listBorrows(@RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "ACTIVE") String status,
                              Model model) {
        String normalizedStatus = status != null ? status.toUpperCase() : "ACTIVE";
        model.addAttribute("borrows", borrowService.searchBorrows(keyword, normalizedStatus));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", normalizedStatus);
        model.addAttribute("activeCount", borrowService.countActiveBorrows());
        model.addAttribute("overdueCount", borrowService.countOverdueBorrows());
        model.addAttribute("returnedCount", borrowService.countReturnedBorrows());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("activeMenu", "borrows");
        return "borrows/list";
    }

    @GetMapping("/new")
    public String showBorrowForm(@RequestParam(required = false) Long bookId, Model model) {
        BorrowFormDto formDto = new BorrowFormDto();
        if (bookId != null) {
            formDto.setBookId(bookId);
        }
        model.addAttribute("borrowForm", formDto);
        model.addAttribute("books", bookService.findAllBooks());
        model.addAttribute("activeMenu", "borrows");
        return "borrows/form";
    }

    @PostMapping
    public String createBorrow(@Valid @ModelAttribute("borrowForm") BorrowFormDto borrowForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (borrowForm.getBorrowDate() != null
                && borrowForm.getDueDate() != null
                && borrowForm.getDueDate().isBefore(borrowForm.getBorrowDate())) {
            bindingResult.rejectValue("dueDate", "error.borrowForm", "Hạn trả phải lớn hơn hoặc bằng ngày mượn");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("activeMenu", "borrows");
            return "borrows/form";
        }

        try {
            Borrow created = borrowService.borrowBook(borrowForm);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã tạo phiếu mượn sách \"" + created.getBook().getTitle()
                    + "\" cho \"" + created.getBorrowerName() + "\" (" + created.getBorrowerEmail() + ") thành công!"
            );
            return "redirect:/borrows";
        } catch (BusinessException ex) {
            if (ex.getFieldName() != null) {
                bindingResult.rejectValue(ex.getFieldName(), "error.borrowForm", ex.getMessage());
            }
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("books", bookService.findAllBooks());
            model.addAttribute("activeMenu", "borrows");
            return "borrows/form";
        }
    }

    @PostMapping("/{id}/return")
    public String returnBook(@PathVariable Long id,
                             @RequestParam(defaultValue = "ACTIVE") String status,
                             RedirectAttributes redirectAttributes) {
        try {
            Borrow returned = borrowService.returnBook(id);
            redirectAttributes.addFlashAttribute(
                "successMessage",
                "Đã ghi nhận trả sách \"" + returned.getBook().getTitle()
                    + "\" của người mượn \"" + returned.getBorrowerName() + "\" thành công! Số lượng sách trong kho đã được cộng lại."
            );
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/borrows?status=" + status;
    }
}

