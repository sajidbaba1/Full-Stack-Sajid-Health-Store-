package com.healthstore.controller;

import com.healthstore.dto.PaymentRequestDTO;
import com.healthstore.model.Order;
import com.healthstore.service.OrderService;
import com.healthstore.service.StripeService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private StripeService stripeService;

    @Autowired
    private OrderService orderService;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<String> createCheckoutSession(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        try {
            Order order = orderService.findById(paymentRequestDTO.getOrderId());

            List<SessionCreateParams.LineItem> lineItems = order.getOrderItems().stream().map(orderItem ->
                SessionCreateParams.LineItem.builder()
                    .setQuantity((long) orderItem.getQuantity())
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("usd")
                            .setUnitAmount(orderItem.getPrice().multiply(BigDecimal.valueOf(100)).longValue())
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(orderItem.getProductName())
                                    .build())
                            .build())
                    .build()
            ).collect(Collectors.toList());

            Session session = stripeService.createCheckoutSession(lineItems, paymentRequestDTO.getSuccessUrl(), paymentRequestDTO.getCancelUrl());
            return ResponseEntity.ok(session.getUrl());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating checkout session: " + e.getMessage());
        }
    }
}
