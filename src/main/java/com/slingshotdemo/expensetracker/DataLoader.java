package com.slingshotdemo.expensetracker;

import com.slingshotdemo.expensetracker.model.Category;
import com.slingshotdemo.expensetracker.model.Expense;
import com.slingshotdemo.expensetracker.model.User;
import com.slingshotdemo.expensetracker.repository.CategoryRepository;
import com.slingshotdemo.expensetracker.repository.ExpenseRepository;
import com.slingshotdemo.expensetracker.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner init(UserRepository userRepository, CategoryRepository categoryRepository, ExpenseRepository expenseRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User u = new User("alice", "alice@example.com", "$2a$10$DOWSDn1vK1f8GqfZ4bpuEu3rVNp5hEJ9QpZp2q6Q1JcY1F0U0xvKq"); // password: password
                userRepository.save(u);

                Category c1 = new Category("Food", u);
                Category c2 = new Category("Transport", u);
                categoryRepository.save(c1);
                categoryRepository.save(c2);

                expenseRepository.save(new Expense(new BigDecimal("12.50"), LocalDate.now().minusDays(1), c1, "Lunch", u));
                expenseRepository.save(new Expense(new BigDecimal("3.75"), LocalDate.now(), c2, "Bus", u));
            }
        };
    }
}

