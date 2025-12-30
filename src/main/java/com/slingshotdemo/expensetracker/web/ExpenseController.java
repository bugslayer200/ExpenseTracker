package com.slingshotdemo.expensetracker.web;

import com.slingshotdemo.expensetracker.model.Category;
import com.slingshotdemo.expensetracker.model.Expense;
import com.slingshotdemo.expensetracker.model.User;
import com.slingshotdemo.expensetracker.service.CategoryService;
import com.slingshotdemo.expensetracker.service.ExpenseService;
import com.slingshotdemo.expensetracker.service.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final UserService userService;
    private final CategoryService categoryService;

    public ExpenseController(ExpenseService expenseService, UserService userService, CategoryService categoryService) {
        this.expenseService = expenseService;
        this.userService = userService;
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        BigDecimal amount = new BigDecimal(body.get("amount").toString());
        LocalDate date = LocalDate.parse(body.getOrDefault("date", LocalDate.now().toString()).toString());
        Category cat = null;
        
        // Handle category by ID if provided, otherwise by name
        if (body.containsKey("categoryId")) {
            Long categoryId = Long.valueOf(body.get("categoryId").toString());
            cat = categoryService.findById(categoryId).orElse(null);
            if (cat != null && !cat.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Category does not belong to user"));
            }
        } else if (body.containsKey("category")) {
            String categoryName = body.get("category").toString();
            if (categoryName != null && !categoryName.isEmpty()) {
                cat = new Category(categoryName, user);
                cat = categoryService.save(cat);
            }
        }
        
        Expense e = new Expense(amount, date, cat, body.getOrDefault("note", "").toString(), user);
        e = expenseService.save(e);
        return ResponseEntity.ok(e);
    }

    @GetMapping
    public List<Expense> list(Authentication auth,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
                              @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        if (start != null && end != null) {
            return expenseService.findBetween(user, start, end);
        }
        return expenseService.findAllForUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Expense e = expenseService.findById(id).orElseThrow();
        if (!e.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        expenseService.delete(e);
        return ResponseEntity.ok(Map.of("deleted", true));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Map<String, String> body, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Expense e = expenseService.findById(id).orElseThrow();
        if (!e.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        if (body.containsKey("amount")) {
            e.setAmount(new BigDecimal(body.get("amount")));
        }
        if (body.containsKey("date")) {
            e.setDate(LocalDate.parse(body.get("date")));
        }
        if (body.containsKey("note")) {
            e.setNote(body.get("note"));
        }
        if (body.containsKey("categoryId")) {
            Long categoryId = Long.valueOf(body.get("categoryId"));
            Category cat = categoryService.findById(categoryId).orElse(null);
            e.setCategory(cat);
        }
        e = expenseService.save(e);
        return ResponseEntity.ok(e);
    }
}

