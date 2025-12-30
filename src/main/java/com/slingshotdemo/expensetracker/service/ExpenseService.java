package com.slingshotdemo.expensetracker.service;

import com.slingshotdemo.expensetracker.model.Expense;
import com.slingshotdemo.expensetracker.model.User;
import com.slingshotdemo.expensetracker.repository.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense save(Expense e) {
        return expenseRepository.save(e);
    }

    public Optional<Expense> findById(Long id) {
        return expenseRepository.findById(id);
    }

    public List<Expense> findAllForUser(User user) {
        return expenseRepository.findAllByUser(user);
    }

    public List<Expense> findBetween(User user, LocalDate start, LocalDate end) {
        return expenseRepository.findAllByUserAndDateBetween(user, start, end);
    }

    public void delete(Expense e) {
        expenseRepository.delete(e);
    }
}

