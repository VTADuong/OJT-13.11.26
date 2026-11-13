package com.example.bookstore.service;

import com.example.bookstore.dto.BorrowFormDto;
import com.example.bookstore.entity.Borrow;

import java.util.List;

public interface BorrowService {

    List<Borrow> searchBorrows(String keyword, String status);

    Borrow findById(Long id);

    Borrow borrowBook(BorrowFormDto formDto);

    Borrow returnBook(Long borrowId);

    long countActiveBorrows();

    long countOverdueBorrows();

    long countReturnedBorrows();
}

