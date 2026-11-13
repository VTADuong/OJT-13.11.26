package com.example.bookstore.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex,
                                         HttpServletRequest request,
                                         Model model) {
        model.addAttribute("errorTitle", "Không tìm thấy dữ liệu (404)");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException ex,
                                        HttpServletRequest request,
                                        Model model) {
        model.addAttribute("errorTitle", "Trang bạn tìm kiếm không tồn tại (404)");
        model.addAttribute("errorMessage", "Đường dẫn bạn vừa truy cập không tồn tại hoặc đã bị di chuyển.");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex,
                                         HttpServletRequest request,
                                         Model model) {
        model.addAttribute("errorTitle", "Đã xảy ra lỗi hệ thống (500)");
        model.addAttribute("errorMessage", ex.getMessage() != null ? ex.getMessage() : "Có lỗi không mong muốn xảy ra, vui lòng thử lại sau.");
        model.addAttribute("requestUri", request.getRequestURI());
        return "error/500";
    }
}

