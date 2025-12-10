// // Without Payment Gateway and Cash on Delivery
// package com.pos_billing.pos_backend.service;

// import com.pos_billing.pos_backend.dto.CreateOrderRequest;
// import com.pos_billing.pos_backend.dto.OrderItemResponse;
// import com.pos_billing.pos_backend.dto.OrderResponse;

// import com.pos_billing.pos_backend.model.Order;
// import com.pos_billing.pos_backend.model.OrderItem;
// import com.pos_billing.pos_backend.model.Product;
// import com.pos_billing.pos_backend.model.Customer;
// import com.pos_billing.pos_backend.model.User;

// import com.pos_billing.pos_backend.repository.OrderItemRepository;
// import com.pos_billing.pos_backend.repository.OrderRepository;
// import com.pos_billing.pos_backend.repository.ProductRepository;
// import com.pos_billing.pos_backend.repository.UserRepository;
// import com.pos_billing.pos_backend.repository.CustomerRepository;

// import lombok.RequiredArgsConstructor;

// import org.springframework.security.core.Authentication;
// import org.springframework.stereotype.Service;

// import java.util.*;
// import java.util.stream.Collectors;

// @Service
// @RequiredArgsConstructor
// public class OrderService {

//     private final OrderRepository orderRepository;
//     private final OrderItemRepository orderItemRepository;
//     private final ProductRepository productRepository;

//     private final UserRepository userRepository;
//     private final CustomerRepository customerRepository;

//     // ⭐ 1️⃣ Create Order
//     public Order createOrder(CreateOrderRequest req, Authentication auth) {

//         // 🔥 Get logged-in customer
//         String email = auth.getName();
//         User user = userRepository.findByEmail(email)
//                 .orElseThrow(() -> new RuntimeException("User not found"));

//         Customer customer = customerRepository.findByUserId(user.getId());
//         if (customer == null) {
//             throw new RuntimeException("Customer profile not found");
//         }

//         // 🔥 Save Order
//         Order order = Order.builder()
//                 .customer(customer)
//                 .paymentMethod(req.getPaymentMethod())
//                 .totalAmount(req.getTotalAmount())
//                 .build();

//         Order savedOrder = orderRepository.save(order);

//         // 🔥 Save Items + Reduce Stock
//         List<OrderItem> items = req.getItems().stream()
//                 .map(itemReq -> {

//                     Product p = productRepository.findById(itemReq.getProductId())
//                             .orElseThrow(() -> new RuntimeException("Product not found"));

//                     if (p.getStock() < itemReq.getQuantity()) {
//                         throw new RuntimeException("Not enough stock for " + p.getName());
//                     }

//                     // reduce stock
//                     p.setStock(p.getStock() - itemReq.getQuantity());
//                     productRepository.save(p);

//                     return OrderItem.builder()
//                             .order(savedOrder)
//                             .productId(itemReq.getProductId())
//                             .productName(p.getName()) // always from DB
//                             .price(itemReq.getPrice())
//                             .quantity(itemReq.getQuantity())
//                             .status("PENDING")
//                             .build();
//                 })
//                 .collect(Collectors.toList());

//         orderItemRepository.saveAll(items);
//         savedOrder.setItems(items);

//         return savedOrder;
//     }

//     // ⭐ 2️⃣ Convert Order → OrderResponse (for UI)
//     // public OrderResponse mapToOrderResponse(Order order) {

//     // OrderResponse res = new OrderResponse();

//     // res.setOrderId(order.getId());
//     // res.setTotalAmount(order.getTotalAmount());
//     // res.setPaymentMethod(order.getPaymentMethod());
//     // res.setCreatedAt(order.getCreatedAt());

//     // // Customer details
//     // Customer c = order.getCustomer();
//     // res.setCustomerName(c.getName());
//     // res.setCustomerPhone(c.getPhone());
//     // res.setCustomerAddress(c.getAddress());

//     // // Items
//     // List<OrderItemResponse> itemList = order.getItems().stream().map(i -> {
//     // OrderItemResponse dto = new OrderItemResponse();
//     // dto.setId(i.getId());
//     // dto.setProductId(i.getProductId());
//     // dto.setProductName(i.getProductName());
//     // dto.setPrice(i.getPrice());
//     // dto.setQuantity(i.getQuantity());
//     // dto.setStatus(i.getStatus());
//     // return dto;
//     // }).collect(Collectors.toList());

//     // res.setItems(itemList);

//     // return res;
//     // }

//     // ⭐ 2️⃣ Convert Order → OrderResponse (for UI)
//     public OrderResponse mapToOrderResponse(Order order) {

//         OrderResponse res = new OrderResponse();

//         res.setOrderId(order.getId());
//         res.setTotalAmount(order.getTotalAmount());
//         res.setPaymentMethod(order.getPaymentMethod());
//         res.setCreatedAt(order.getCreatedAt());

//         // -------------------------------
//         // ✅ SAFE Customer Handling
//         // -------------------------------
//         Customer c = order.getCustomer();

//         res.setCustomerName(c != null ? c.getName() : "Unknown");
//         res.setCustomerPhone(c != null ? c.getPhone() : "N/A");
//         res.setCustomerAddress(c != null ? c.getAddress() : "N/A");

//         // -------------------------------
//         // Items
//         // -------------------------------
//         List<OrderItemResponse> itemList = order.getItems() != null
//                 ? order.getItems().stream().map(i -> {
//                     OrderItemResponse dto = new OrderItemResponse();
//                     dto.setId(i.getId());
//                     dto.setProductId(i.getProductId());
//                     dto.setProductName(i.getProductName());
//                     dto.setPrice(i.getPrice());
//                     dto.setQuantity(i.getQuantity());
//                     dto.setStatus(i.getStatus());
//                     return dto;
//                 }).collect(Collectors.toList())
//                 : List.of();

//         res.setItems(itemList);

//         return res;
//     }

//     // ⭐ 3️⃣ Get All Orders — Admin
//     public List<OrderResponse> getAllOrders() {
//         return orderRepository.findAll()
//                 .stream()
//                 .map(this::mapToOrderResponse)
//                 .collect(Collectors.toList());
//     }

//     public List<OrderResponse> getOrdersByCustomer(Long customerId) {
//         return orderRepository.findByCustomer_Id(customerId)
//                 .stream()
//                 .map(this::mapToOrderResponse)
//                 .collect(Collectors.toList());
//     }

//     // ⭐ 4️⃣ Count Orders — Dashboard
//     public long countOrders() {
//         return orderRepository.count();
//     }

//     // ⭐ 5️⃣ Total Sales — Dashboard
//     public double totalSalesAmount() {
//         Double total = orderRepository.sumTotalAmount();
//         return total != null ? total : 0.0;
//     }

//     // ⭐ 6️⃣ Weekly Sales — Dashboard
//     public List<Map<String, Object>> getWeeklySales() {
//         List<Object[]> rows = orderRepository.weeklySalesRaw();
//         List<Map<String, Object>> result = new ArrayList<>();

//         for (Object[] row : rows) {
//             Map<String, Object> map = new HashMap<>();
//             map.put("day", row[0]);
//             map.put("sales", row[1]);
//             result.add(map);
//         }
//         return result;
//     }
// }

// // With Payment Gateway and Cash on delivery

package com.pos_billing.pos_backend.service;

import com.pos_billing.pos_backend.dto.CreateOrderRequest;
import com.pos_billing.pos_backend.dto.OrderItemResponse;
import com.pos_billing.pos_backend.dto.OrderResponse;

import com.pos_billing.pos_backend.model.Order;
import com.pos_billing.pos_backend.model.OrderItem;
import com.pos_billing.pos_backend.model.Product;
import com.pos_billing.pos_backend.model.Customer;
import com.pos_billing.pos_backend.model.User;

import com.pos_billing.pos_backend.repository.OrderItemRepository;
import com.pos_billing.pos_backend.repository.OrderRepository;
import com.pos_billing.pos_backend.repository.ProductRepository;
import com.pos_billing.pos_backend.repository.UserRepository;
import com.pos_billing.pos_backend.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;

    // =====================================================================================
    // ⭐ 1️⃣ Original Create Order (USED FOR ONLINE PAYMENT ONLY)
    // =====================================================================================
    @SuppressWarnings("DuplicatedCode")
    public Order createOrder(CreateOrderRequest req, Authentication auth) {

        // Get logged-in customer
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId());
        if (customer == null) {
            throw new RuntimeException("Customer profile not found");
        }

        // Save Order
        Order order = Order.builder()
                .customer(customer)
                .paymentMethod(req.getPaymentMethod()) // UPI, CARD, ONLINE, etc.
                .totalAmount(req.getTotalAmount())
                .build();

        Order savedOrder = orderRepository.save(order);

        // Save Items + Reduce Stock
        List<OrderItem> items = req.getItems().stream()
                .map(itemReq -> {

                    Product p = productRepository.findById(itemReq.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    if (p.getStock() < itemReq.getQuantity()) {
                        throw new RuntimeException("Not enough stock for " + p.getName());
                    }

                    // Reduce stock
                    p.setStock(p.getStock() - itemReq.getQuantity());
                    productRepository.save(p);

                    return OrderItem.builder()
                            .order(savedOrder)
                            .productId(itemReq.getProductId())
                            .productName(p.getName())
                            .price(itemReq.getPrice())
                            .quantity(itemReq.getQuantity())
                            .status("PENDING")
                            .build();
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(items);
        savedOrder.setItems(items);

        return savedOrder;
    }

    // =====================================================================================
    // ⭐ 2️⃣ New Create COD Order (ONLY FOR CASH ON DELIVERY)
    // =====================================================================================
    @SuppressWarnings("DuplicatedCode")
    public OrderResponse createCodOrder(CreateOrderRequest req, Authentication auth) {

        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId());
        if (customer == null) {
            throw new RuntimeException("Customer profile not found");
        }

        // Create Order (PENDING because COD)
        Order order = new Order();
        order.setCustomer(customer);
        order.setPaymentMethod("CASH_ON_DELIVERY");
        // order.setStatus("PENDING");
        order.setTotalAmount(req.getTotalAmount());

        Order savedOrder = orderRepository.save(order);

        // Save Items + Reduce Stock
        List<OrderItem> items = req.getItems().stream()
                .map(itemReq -> {

                    Product p = productRepository.findById(itemReq.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    if (p.getStock() < itemReq.getQuantity()) {
                        throw new RuntimeException("Not enough stock for " + p.getName());
                    }

                    p.setStock(p.getStock() - itemReq.getQuantity());
                    productRepository.save(p);

                    return OrderItem.builder()
                            .order(savedOrder)
                            .productId(itemReq.getProductId())
                            .productName(p.getName())
                            .price(itemReq.getPrice())
                            .quantity(itemReq.getQuantity())
                            .status("PENDING")
                            .build();
                })
                .collect(Collectors.toList());

        orderItemRepository.saveAll(items);
        savedOrder.setItems(items);

        return mapToOrderResponse(savedOrder);
    }

    // =====================================================================================
    // ⭐ 3️⃣ Convert Order → OrderResponse
    // =====================================================================================
    public OrderResponse mapToOrderResponse(Order order) {

        OrderResponse res = new OrderResponse();

        res.setOrderId(order.getId());
        res.setTotalAmount(order.getTotalAmount());
        res.setPaymentMethod(order.getPaymentMethod());
        res.setCreatedAt(order.getCreatedAt());

        // Customer details safe
        Customer c = order.getCustomer();
        res.setCustomerName(c != null ? c.getName() : "Unknown");
        res.setCustomerPhone(c != null ? c.getPhone() : "N/A");
        res.setCustomerAddress(c != null ? c.getAddress() : "N/A");

        // Items
        if (order.getItems() != null) {
            List<OrderItemResponse> list = order.getItems().stream()
                    .map(i -> {
                        OrderItemResponse dto = new OrderItemResponse();
                        dto.setId(i.getId());
                        dto.setProductId(i.getProductId());
                        dto.setProductName(i.getProductName());
                        dto.setPrice(i.getPrice());
                        dto.setQuantity(i.getQuantity());
                        dto.setStatus(i.getStatus());
                        return dto;
                    }).collect(Collectors.toList());

            res.setItems(list);
        } else {
            res.setItems(new ArrayList<>());
        }

        return res;
    }

    // =====================================================================================
    // ⭐ 4️⃣ Admin Get All Orders
    // =====================================================================================
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // =====================================================================================
    // ⭐ 5️⃣ Customer Get My Orders
    // =====================================================================================
    public List<OrderResponse> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomer_Id(customerId)
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    // =====================================================================================
    // ⭐ 6️⃣ Dashboard: Total Orders Count
    // =====================================================================================
    public long countOrders() {
        return orderRepository.count();
    }

    // =====================================================================================
    // ⭐ 7️⃣ Dashboard: Total Sales
    // =====================================================================================
    public double totalSalesAmount() {
        Double total = orderRepository.sumTotalAmount();
        return total != null ? total : 0.0;
    }

    // =====================================================================================
    // ⭐ 8️⃣ Dashboard: Weekly Sales Summary
    // =====================================================================================
    public List<Map<String, Object>> getWeeklySales() {
        List<Object[]> rows = orderRepository.weeklySalesRaw();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> map = new HashMap<>();
            map.put("day", row[0]);
            map.put("sales", row[1]);
            result.add(map);
        }

        return result;
    }
}

