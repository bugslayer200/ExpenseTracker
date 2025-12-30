package com.slingshotdemo.expensetracker.repository;

import com.slingshotdemo.expensetracker.model.Expense;
import com.slingshotdemo.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findAllByUser(User user);
    List<Expense> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);
}

