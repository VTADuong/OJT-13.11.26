package com.example.bookstore.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object uriObj = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String requestUri = uriObj != null ? uriObj.toString() : request.getRequestURI();

        if (statusObj != null) {
            int statusCode = Integer.parseInt(statusObj.toString());
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("errorTitle", "Trang bạn tìm kiếm không tồn tại (404)");
                model.addAttribute("errorMessage", "Đường dẫn bạn truy cập không tồn tại hoặc tài nguyên đã bị xoá.");
                model.addAttribute("requestUri", requestUri);
                return "error/404";
            }
        }

        model.addAttribute("errorTitle", "Đã xảy ra lỗi xử lý yêu cầu");
        model.addAttribute("errorMessage", "Vui lòng kiểm tra lại thao tác hoặc quay về trang chủ.");
        model.addAttribute("requestUri", requestUri);
        return "error/500";
    }
}

