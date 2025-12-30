package com.slingshotdemo.expensetracker.service;

import com.slingshotdemo.expensetracker.model.Category;
import com.slingshotdemo.expensetracker.model.User;
import com.slingshotdemo.expensetracker.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category save(Category c) {
        return categoryRepository.save(c);
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    public List<Category> findAllForUser(User user) {
        return categoryRepository.findAllByUser(user);
    }

    public void delete(Category c) {
        categoryRepository.delete(c);
    }
}

