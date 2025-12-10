package com.pos_billing.pos_backend.repository;

import com.pos_billing.pos_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByUserId(Long userId);
}
