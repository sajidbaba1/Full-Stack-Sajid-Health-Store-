package com.healthstore.controller;

import com.healthstore.model.Order;
import com.healthstore.service.OrderService;
import com.healthstore.service.PaymentService;
import com.healthstore.service.EmailService;
import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling Stripe webhook events.
 * This controller processes various Stripe webhook events and updates the application state accordingly.
 */
@Slf4j
@RestController
@RequestMapping("/api/webhooks")
@RequiredArgsConstructor
public class StripeWebhookController {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookController.class);

    @Value("${stripe.secret-key}")
    private String stripeApiKey;
    
    @Value("${stripe.webhook.secret}")
    private String stripeWebhookSecret;

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final EmailService emailService;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    /**
     * Handles incoming Stripe webhook events.
     * 
     * @param payload The raw webhook payload
     * @param sigHeader The Stripe-Signature header for verification
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        
        Event event;
        Map<String, String> response = new HashMap<>();
        
        try {
            // Verify the webhook signature
            event = Webhook.constructEvent(payload, sigHeader, stripeWebhookSecret);
            log.info("Received Stripe webhook event: {}", event.getType());
            
            // Process the event based on its type
            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(event);
                    break;
                    
                case "payment_intent.succeeded":
                    handlePaymentIntentSucceeded(event);
                    break;
                    
                case "payment_intent.payment_failed":
                    handlePaymentIntentFailed(event);
                    break;
                    
                case "charge.refunded":
                    handleChargeRefunded(event);
                    break;
                    
                case "charge.dispute.created":
                    handleDisputeCreated(event);
                    break;
                    
                // Add more event types as needed
                
                default:
                    log.info("Unhandled event type: {}", event.getType());
            }
            
            response.put("status", "success");
            response.put("message", "Webhook processed successfully");
            return ResponseEntity.ok(response);
            
        } catch (SignatureVerificationException e) {
            log.error("Invalid webhook signature", e);
            response.put("status", "error");
            response.put("message", "Invalid webhook signature");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            
        } catch (StripeException e) {
            log.error("Stripe API error", e);
            response.put("status", "error");
            response.put("message", "Stripe API error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            
        } catch (Exception e) {
            log.error("Error processing webhook", e);
            response.put("status", "error");
            response.put("message", "Error processing webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Handles the checkout.session.completed event.
     * This event is sent when a customer completes the checkout process.
     */
    private void handleCheckoutSessionCompleted(Event event) throws StripeException {
        Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
        if (session == null || session.getClientReferenceId() == null) {
            log.warn("No client reference ID in session");
            return;
        }
        
        try {
            Long orderId = Long.parseLong(session.getClientReferenceId());
            log.info("Processing checkout.session.completed for order: {}", orderId);
            
            // Get the order to update and send email
            Order order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
                
            // Update order status to PROCESSING (payment is not yet confirmed)
            orderService.updateOrderStatus(orderId, Order.OrderStatus.PROCESSING);
            
            // Store the Stripe payment intent ID with the order for future reference
            if (session.getPaymentIntent() != null) {
                orderService.updateOrderPaymentInfo(orderId, session.getPaymentIntent(), session.getPaymentStatus());
            }
            
            // Send order confirmation email
            try {
                emailService.sendOrderConfirmation(order);
                log.info("Sent order confirmation email for order {}", orderId);
            } catch (Exception e) {
                log.error("Failed to send order confirmation email for order {}: {}", 
                         orderId, e.getMessage(), e);
            }
            
            log.info("Successfully processed checkout.session.completed for order: {}", orderId);
            
        } catch (NumberFormatException e) {
            log.error("Invalid order ID in client reference: {}", session.getClientReferenceId(), e);
        } catch (Exception e) {
            log.error("Error processing checkout.session.completed event", e);
            throw e;
        }
    }
    
    /**
     * Handles the payment_intent.succeeded event.
     * This event is sent when a payment is successfully completed.
     */
    private void handlePaymentIntentSucceeded(Event event) throws StripeException {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent == null) {
            log.warn("No payment intent in event");
            return;
        }
        
        try {
            // Find the order associated with this payment intent
            String paymentIntentId = paymentIntent.getId();
            log.info("Processing payment_intent.succeeded for payment intent: {}", paymentIntentId);
            
            // Update the order status to PAID and record payment details
            paymentService.handleSuccessfulPayment(paymentIntent);
            
            log.info("Successfully processed payment_intent.succeeded for payment intent: {}", paymentIntentId);
            
        } catch (Exception e) {
            log.error("Error processing payment_intent.succeeded event", e);
            throw e;
        }
    }
    
    /**
     * Handles the payment_intent.payment_failed event.
     * This event is sent when a payment attempt fails.
     */
    private void handlePaymentIntentFailed(Event event) throws StripeException {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElse(null);
        if (paymentIntent == null) {
            log.warn("No payment intent in event");
            return;
        }
        
        try {
            String paymentIntentId = paymentIntent.getId();
            log.warn("Processing payment_intent.payment_failed for payment intent: {}", paymentIntentId);
            
            // Update the order status to reflect the payment failure
            paymentService.handleFailedPayment(paymentIntent);
            
            log.info("Processed payment_intent.payment_failed for payment intent: {}", paymentIntentId);
            
        } catch (Exception e) {
            log.error("Error processing payment_intent.payment_failed event", e);
            throw e;
        }
    }
    
    /**
     * Handles the charge.refunded event.
     * This event is sent when a charge is refunded.
     */
    private void handleChargeRefunded(Event event) throws StripeException {
        Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElse(null);
        if (charge == null) {
            log.warn("No charge in event");
            return;
        }
        
        try {
            String chargeId = charge.getId();
            log.info("Processing charge.refunded for charge: {}", chargeId);
            
            // Update the order status to REFUNDED
            paymentService.handleRefund(charge);
            
            log.info("Successfully processed charge.refunded for charge: {}", chargeId);
            
        } catch (Exception e) {
            log.error("Error processing charge.refunded event", e);
            throw e;
        }
    }
    
    /**
     * Handles the charge.dispute.created event.
     * This event is sent when a customer disputes a charge.
     */
    private void handleDisputeCreated(Event event) throws StripeException {
        Dispute dispute = (Dispute) event.getDataObjectDeserializer().getObject().orElse(null);
        if (dispute == null) {
            log.warn("No dispute in event");
            return;
        }
        
        try {
            String disputeId = dispute.getId();
            log.warn("Processing charge.dispute.created for dispute: {}", disputeId);
            
            // Update the order status to DISPUTED
            paymentService.handleDispute(dispute);
            
            log.info("Processed charge.dispute.created for dispute: {}", disputeId);
            
        } catch (Exception e) {
            log.error("Error processing charge.dispute.created event", e);
            throw e;
        }
    }
}
