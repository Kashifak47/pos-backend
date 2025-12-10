package com.pos_billing.pos_backend.service;

import com.pos_billing.pos_backend.model.Customer;
import com.pos_billing.pos_backend.model.User;
import com.pos_billing.pos_backend.repository.CustomerRepository;
import com.pos_billing.pos_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public List<Customer> getAll() {
        return customerRepo.findAll();
    }

    public Customer getById(Long id) {
        return customerRepo.findById(id).orElse(null);
    }

    public Customer getByUserId(Long userId) {
        return customerRepo.findByUserId(userId);
    }

    public Customer addCustomer(Customer c, String password) {

        // 1️⃣ Create a user in users table
        User user = User.builder()
                .name(c.getName())
                .email(c.getEmail())
                .password(encoder.encode(password))
                .role("CUSTOMER")
                .build();

        userRepo.save(user);

        // 2️⃣ Link customer with userId
        c.setUserId(user.getId());

        return customerRepo.save(c);
    }

    public long countCustomers() {
        return customerRepo.count();
    }

    public Customer updateCustomer(Long id, Customer c) {
        return customerRepo.findById(id).map(existing -> {
            existing.setName(c.getName());
            existing.setPhone(c.getPhone());
            existing.setEmail(c.getEmail());
            existing.setAddress(c.getAddress());
            return customerRepo.save(existing);
        }).orElse(null);
    }

    public void deleteCustomer(Long id) {
        customerRepo.deleteById(id);
    }
}
