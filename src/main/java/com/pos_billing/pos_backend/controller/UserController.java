package com.pos_billing.pos_backend.controller;

import com.pos_billing.pos_backend.dto.LoginRequest;
import com.pos_billing.pos_backend.dto.LoginResponse;
import com.pos_billing.pos_backend.dto.RegisterRequest;
import com.pos_billing.pos_backend.model.User;
import com.pos_billing.pos_backend.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
// @CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    // ⭐ REGISTER
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> registerUser(@RequestBody RegisterRequest req) {

        userService.registerUser(req);

        return ResponseEntity.ok(
                new LoginResponse(
                        "User registered successfully!",
                        true,
                        "CUSTOMER"
                )
        );
    }

    // ⭐ LOGIN
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {

        Optional<User> userOpt = userService.login(request.getEmail(), request.getPassword());

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            return ResponseEntity.ok(
                    new LoginResponse(
                            "Login successful!",
                            true,
                            user.getRole()
                    )
            );
        }

        return ResponseEntity.status(401)
                .body(new LoginResponse("Invalid credentials", false, null));
    }
}
