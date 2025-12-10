package com.pos_billing.pos_backend.service;

import com.pos_billing.pos_backend.dto.RegisterRequest;
import com.pos_billing.pos_backend.model.Customer;
import com.pos_billing.pos_backend.model.User;
import com.pos_billing.pos_backend.repository.CustomerRepository;
import com.pos_billing.pos_backend.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ⭐ REGISTER USER
    public void registerUser(RegisterRequest req) {

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .phone(req.getPhone())
                .address(req.getAddress())
                .role("CUSTOMER")
                .build();

        User savedUser = userRepository.save(user);

        // ⭐ Auto-create linked customer profile
        Customer customer = Customer.builder()
                .userId(savedUser.getId())
                .name(req.getName())
                .email(req.getEmail())
                .phone(req.getPhone())
                .address(req.getAddress())
                .build();

        customerRepository.save(customer);
    }


    // ⭐ LOGIN
    public Optional<User> login(String email, String rawPassword) {

        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }
}
