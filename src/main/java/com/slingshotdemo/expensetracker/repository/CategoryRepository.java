package com.slingshotdemo.expensetracker.repository;

import com.slingshotdemo.expensetracker.model.Category;
import com.slingshotdemo.expensetracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUser(User user);
}

