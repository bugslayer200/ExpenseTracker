package com.slingshotdemo.expensetracker.web;

import com.slingshotdemo.expensetracker.model.User;
import com.slingshotdemo.expensetracker.service.UserService;
import com.slingshotdemo.expensetracker.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    record RegisterRequest(String username, String email, String password) {}
    record LoginRequest(String username, String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequest req) {
        if (userService.findByUsername(req.username()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username_taken"));
        }
        if (userService.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "email_taken"));
        }
        User u = userService.register(req.username(), req.email(), req.password());
        String token = jwtUtil.generateToken(u.getUsername());
        return ResponseEntity.ok(Map.of(
                "token", token,
                "user", Map.of("id", u.getId(), "username", u.getUsername(), "email", u.getEmail())
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.username(), req.password())
            );
            User u = userService.findByUsername(req.username()).orElseThrow();
            String token = jwtUtil.generateToken(u.getUsername());
            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "user", Map.of("id", u.getId(), "username", u.getUsername(), "email", u.getEmail())
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }
}


