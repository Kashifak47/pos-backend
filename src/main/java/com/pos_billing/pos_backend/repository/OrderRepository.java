package com.pos_billing.pos_backend.repository;

import com.pos_billing.pos_backend.model.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderRepository extends JpaRepository<Order, Long> {
    // Total Sales (SUM of all order amounts)
    @Query("SELECT SUM(o.totalAmount) FROM Order o")
    Double sumTotalAmount();

    // Weekly Sales Raw Data (SQL)
    @Query(value = """
            SELECT day, sales FROM (
                SELECT
                    DAYNAME(created_at) AS day,
                    SUM(total_amount) AS sales
                FROM orders
                WHERE created_at >= DATE_SUB(CURDATE(), INTERVAL 7 DAY)
                GROUP BY DAYNAME(created_at)
            ) AS t
            ORDER BY FIELD(
                day,
                'Monday','Tuesday','Wednesday','Thursday','Friday','Saturday','Sunday'
            )
            """, nativeQuery = true)
    List<Object[]> weeklySalesRaw();

    List<Order> findByCustomer_Id(Long customerId);

}
