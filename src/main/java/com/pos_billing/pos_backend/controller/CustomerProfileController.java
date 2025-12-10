package com.pos_billing.pos_backend.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.pos_billing.pos_backend.model.Customer;
import com.pos_billing.pos_backend.model.User;
import com.pos_billing.pos_backend.repository.UserRepository;
import com.pos_billing.pos_backend.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
// @CrossOrigin("*")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerService customerService;
    private final UserRepository userRepo;
    // use it later during password update by customer
    // private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // GET PROFILE
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication auth) {

        String email = auth.getName(); // logged-in user

        User user = userRepo.findByEmail(email).orElse(null);
        if (user == null)
            return ResponseEntity.status(404).body("User not found");

        Customer customer = customerService.getByUserId(user.getId());

        return ResponseEntity.ok(customer);
    }

    // UPDATE PROFILE
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Customer updated, Authentication auth) {

        String email = auth.getName();
        User user = userRepo.findByEmail(email).orElse(null);

        Customer existing = customerService.getByUserId(user.getId());
        if (existing == null)
            return ResponseEntity.status(404).body("Customer not found");

        existing.setName(updated.getName());
        existing.setPhone(updated.getPhone());
        existing.setEmail(updated.getEmail());
        existing.setAddress(updated.getAddress());

        customerService.updateCustomer(existing.getId(), existing);

        return ResponseEntity.ok(existing);
    }

    // Change Password will done later
    
    // @PutMapping("/change-password")
    // public ResponseEntity<?> changePassword(@RequestBody Map<String, String> req, Authentication auth) {
    //     String oldPass = req.get("oldPassword");
    //     String newPass = req.get("newPassword");

    //     User user = userRepo.findByEmail(auth.getName()).orElse(null);

    //     if (!encoder.matches(oldPass, user.getPassword())) {
    //         return ResponseEntity.status(400).body("Old password incorrect");
    //     }

    //     user.setPassword(encoder.encode(newPass));
    //     userRepo.save(user);

    //     return ResponseEntity.ok("Password updated");
    // }

}
