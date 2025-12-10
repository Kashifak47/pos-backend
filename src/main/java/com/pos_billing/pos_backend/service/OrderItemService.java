// // Previous Logic For Single Product of order update

// package com.pos_billing.pos_backend.service;

// import com.pos_billing.pos_backend.model.OrderItem;
// import com.pos_billing.pos_backend.repository.OrderItemRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;

// @Service
// public class OrderItemService {

//     @Autowired
//     private OrderItemRepository orderItemRepository;

//     private static final List<String> VALID = List.of(
//             "PENDING", "PREPARING", "COOKING", "READY", "COMPLETED", "CANCELLED"
//     );

//     public String updateItemStatus(Long itemId, String status) {

//         OrderItem item = orderItemRepository.findById(itemId)
//                 .orElseThrow(() -> new RuntimeException("Order item not found"));

//         String upper = status.toUpperCase();

//         if (!VALID.contains(upper)) {
//             throw new RuntimeException("Invalid status: " + status);
//         }

//         item.setStatus(upper);
//         orderItemRepository.save(item);

//         return "Status updated to " + upper;
//     }
// }

// // This logic for Updating whole status of single order

package com.pos_billing.pos_backend.service;

import com.pos_billing.pos_backend.model.Order;
import com.pos_billing.pos_backend.model.OrderItem;
import com.pos_billing.pos_backend.repository.OrderItemRepository;
import com.pos_billing.pos_backend.repository.OrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    private static final List<String> VALID = List.of(
            "PENDING", "PREPARING", "COMPLETED", "CANCELLED"
    );

    public String updateItemStatus(Long itemId, String status) {

        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));

        String upper = status.toUpperCase();
        if (!VALID.contains(upper)) {
            throw new RuntimeException("Invalid status: " + status);
        }

        // ⭐ 1) Get the order of this item
        Order order = item.getOrder();

        if (order == null) {
            throw new RuntimeException("Order not found for this item");
        }

        // ⭐ 2) Get all items of this order
        List<OrderItem> allItems = order.getItems();

        // ⭐ 3) Update status for all items
        for (OrderItem it : allItems) {
            it.setStatus(upper);
        }

        orderItemRepository.saveAll(allItems);

        return "All items of Order #" + order.getId() + " updated to " + upper;
    }
}

