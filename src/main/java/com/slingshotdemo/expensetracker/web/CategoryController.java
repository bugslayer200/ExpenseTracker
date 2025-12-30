package com.slingshotdemo.expensetracker.web;

import com.slingshotdemo.expensetracker.model.Category;
import com.slingshotdemo.expensetracker.model.User;
import com.slingshotdemo.expensetracker.service.CategoryService;
import com.slingshotdemo.expensetracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    public CategoryController(CategoryService categoryService, UserService userService) {
        this.categoryService = categoryService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body, Authentication auth) {
        String name = body.get("name");
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Category c = new Category(name, user);
        c = categoryService.save(c);
        return ResponseEntity.ok(Map.of("id", c.getId(), "name", c.getName()));
    }

    @GetMapping
    public List<Category> list(Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        return categoryService.findAllForUser(user);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        Category c = categoryService.findById(id).orElseThrow();
        if (!c.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }
        categoryService.delete(c);
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}

