package com.pos_billing.pos_backend.service;

import com.pos_billing.pos_backend.dto.PaymentItemDto;
import com.pos_billing.pos_backend.dto.PaymentVerifyRequest;
import com.pos_billing.pos_backend.model.Customer;
import com.pos_billing.pos_backend.model.Order;
import com.pos_billing.pos_backend.model.OrderItem;
import com.pos_billing.pos_backend.model.Product;
import com.pos_billing.pos_backend.model.User;
import com.pos_billing.pos_backend.repository.CustomerRepository;
import com.pos_billing.pos_backend.repository.OrderItemRepository;
import com.pos_billing.pos_backend.repository.OrderRepository;
import com.pos_billing.pos_backend.repository.ProductRepository;
import com.pos_billing.pos_backend.repository.UserRepository;
import com.razorpay.RazorpayClient;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RazorpayService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Value("${RAZORPAY_KEY_ID}")
    private String razorKey;

    @Value("${RAZORPAY_KEY_SECRET}")
    private String razorSecret;

    // =================================================================================
    // ⭐ 1️⃣ Create Razorpay Order for online payment
    // =================================================================================
    public Map<String, Object> createRazorpayOrder(int amountInRupees) {
        try {
            RazorpayClient client = new RazorpayClient(razorKey, razorSecret);

            JSONObject orderReq = new JSONObject();
            orderReq.put("amount", amountInRupees * 100); // convert ₹ → paise
            orderReq.put("currency", "INR");

            com.razorpay.Order razorOrder = client.orders.create(orderReq);

            Map<String, Object> res = new HashMap<>();
            res.put("orderId", razorOrder.get("id"));
            res.put("amount", razorOrder.get("amount")); // in paise
            res.put("currency", razorOrder.get("currency"));
            res.put("key", razorKey);

            return res;

        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    // =================================================================================
    // ⭐ 2️⃣ Verify Razorpay Signature
    // =================================================================================
    private boolean isSignatureValid(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(razorSecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);

            byte[] hashBytes = mac.doFinal(data.getBytes());

            StringBuilder expected = new StringBuilder();
            for (byte b : hashBytes) {
                expected.append(String.format("%02x", b));
            }

            return expected.toString().equals(signature);

        } catch (Exception e) {
            return false;
        }
    }

    // =================================================================================
    // ⭐ 3️⃣ Verify + Create Order + Reduce Stock (for ONLINE)
    // =================================================================================
    public Map<String, Object> verifyAndCreateOrder(PaymentVerifyRequest req, Authentication auth) {

        boolean valid = isSignatureValid(
                req.getRazorpayOrderId(),
                req.getRazorpayPaymentId(),
                req.getRazorpaySignature());

        if (!valid) {
            return Map.of(
                    "status", "FAILED",
                    "message", "Invalid Razorpay signature");
        }

        // ✅ Logged-in user → customer
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Customer customer = customerRepository.findByUserId(user.getId());
        if (customer == null) {
            throw new RuntimeException("Customer profile not found");
        }

        // ✅ Create order with PAID status (online)
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(req.getTotalAmount());
        order.setPaymentMethod(req.getMethod()); // "UPI" or "Card"
        // order.setStatus("PAID");

        Order savedOrder = orderRepository.save(order);

        // ✅ Save items + reduce stock
        List<OrderItem> orderItems = new ArrayList<>();

        for (PaymentItemDto item : req.getItems()) {

            Product p = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (p.getStock() < item.getQty()) {
                throw new RuntimeException("Not enough stock for " + p.getName());
            }

            // Reduce stock AFTER successful payment
            p.setStock(p.getStock() - item.getQty());
            productRepository.save(p);

            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setProductId(p.getId());
            oi.setProductName(p.getName());
            oi.setPrice(item.getPrice());
            oi.setQuantity(item.getQty());
            oi.setStatus("PENDING");

            orderItems.add(oi);
        }

        orderItemRepository.saveAll(orderItems);
        savedOrder.setItems(orderItems);

        return Map.of(
                "status", "SUCCESS",
                "orderId", savedOrder.getId());
    }
}
