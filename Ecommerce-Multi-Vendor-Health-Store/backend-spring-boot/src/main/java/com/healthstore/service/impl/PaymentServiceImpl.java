package com.healthstore.service.impl;

import com.healthstore.model.Order;
import com.healthstore.repository.OrderRepository;
import com.healthstore.service.EmailService;
import com.healthstore.service.OrderService;
import com.healthstore.service.PaymentService;
import com.stripe.model.Charge;
import com.stripe.model.Dispute;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Implementation of the PaymentService interface.
 * Handles payment-related operations and updates the application state accordingly.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final EmailService emailService;

    @Override
    @Transactional
    public void handleSuccessfulPayment(PaymentIntent paymentIntent) {
        String paymentIntentId = paymentIntent.getId();
        log.info("Handling successful payment for payment intent: {}", paymentIntentId);
        
        // Find the order associated with this payment intent
        Optional<Order> orderOpt = orderRepository.findByPaymentIntentId(paymentIntentId);
        
        if (orderOpt.isEmpty()) {
            log.warn("No order found for payment intent: {}", paymentIntentId);
            return;
        }
        
        Order order = orderOpt.get();
        
        // Update order status to PAID
        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.PAID);
        
        // Update payment information
        order.setPaymentStatus("PAID");
        order.setPaymentMethod(paymentIntent.getPaymentMethodTypes() != null && 
                             !paymentIntent.getPaymentMethodTypes().isEmpty() ? 
                             paymentIntent.getPaymentMethodTypes().get(0) : null);
        
        // Set payment ID from payment intent ID since charges may not be available immediately
        order.setPaymentId(paymentIntent.getId());
        order.setPaymentDate(java.time.LocalDateTime.now());
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Successfully updated order {} to PAID status", order.getId());
        
        // Send payment confirmation email
        try {
            emailService.sendPaymentConfirmation(updatedOrder);
            log.info("Sent payment confirmation email for order {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to send payment confirmation email for order {}: {}", 
                     order.getId(), e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void handleFailedPayment(PaymentIntent paymentIntent) {
        String paymentIntentId = paymentIntent.getId();
        log.warn("Handling failed payment for payment intent: {}", paymentIntentId);
        
        // Find the order associated with this payment intent
        Optional<Order> orderOpt = orderRepository.findByPaymentIntentId(paymentIntentId);
        
        if (orderOpt.isEmpty()) {
            log.warn("No order found for failed payment intent: {}", paymentIntentId);
            return;
        }
        
        Order order = orderOpt.get();
        
        // Update order status to PAYMENT_FAILED
        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.PAYMENT_FAILED);
        
        // Update payment information
        order.setPaymentStatus("FAILED");
        
        String errorMessage = null;
        // Add error message if available
        if (paymentIntent.getLastPaymentError() != null) {
            errorMessage = paymentIntent.getLastPaymentError().getMessage();
            order.setPaymentErrorMessage(errorMessage);
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.warn("Updated order {} to PAYMENT_FAILED status", order.getId());
        
        // Send payment failure notification
        try {
            emailService.sendPaymentFailureNotification(updatedOrder, errorMessage);
            log.info("Sent payment failure notification for order {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to send payment failure notification for order {}: {}", 
                     order.getId(), e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void handleRefund(Charge charge) {
        String chargeId = charge.getId();
        log.info("Handling refund for charge: {}", chargeId);
        
        // Find the order associated with this charge
        Optional<Order> orderOpt = orderRepository.findByPaymentId(chargeId);
        
        if (orderOpt.isEmpty()) {
            log.warn("No order found for refunded charge: {}", chargeId);
            return;
        }
        
        Order order = orderOpt.get();
        
        // Update order status to REFUNDED
        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.REFUNDED);
        
        // Get refund amount from the charge
        double refundAmount = 0.0;
        if (charge.getAmountRefunded() > 0) {
            refundAmount = charge.getAmountRefunded() / 100.0; // Convert from cents to dollars
        } else {
            // If no specific refund amount, use the order total
            refundAmount = order.getTotalAmount().doubleValue();
        }
        
        // Update payment information
        order.setPaymentStatus("REFUNDED");
        order.setRefunded(true);
        order.setRefundDate(java.time.LocalDateTime.now());
        order.setRefundAmount(BigDecimal.valueOf(refundAmount));
        
        Order updatedOrder = orderRepository.save(order);
        log.info("Updated order {} to REFUNDED status", order.getId());
        
        // Send refund confirmation email
        try {
            emailService.sendRefundConfirmation(updatedOrder, refundAmount);
            log.info("Sent refund confirmation email for order {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to send refund confirmation email for order {}: {}", 
                     order.getId(), e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional
    public void handleDispute(Dispute dispute) {
        String chargeId = dispute.getCharge();
        log.warn("Handling dispute for charge: {}", chargeId);
        
        // Find the order associated with this charge
        Optional<Order> orderOpt = orderRepository.findByPaymentId(chargeId);
        
        if (orderOpt.isEmpty()) {
            log.warn("No order found for disputed charge: {}", chargeId);
            return;
        }
        
        Order order = orderOpt.get();
        
        // Update order status to DISPUTED
        orderService.updateOrderStatus(order.getId(), Order.OrderStatus.DISPUTED);
        
        // Update payment information
        order.setDisputed(true);
        order.setDisputeReason(dispute.getReason());
        order.setDisputeStatus(dispute.getStatus());
        
        // If the dispute is won, update the order status accordingly
        if ("won".equalsIgnoreCase(dispute.getStatus())) {
            order.setStatus(Order.OrderStatus.REFUNDED);
            order.setRefunded(true);
            order.setRefundDate(java.time.LocalDateTime.now());
        }
        
        Order updatedOrder = orderRepository.save(order);
        log.warn("Updated order {} to DISPUTED status", order.getId());
        
        // Send dispute notification email
        try {
            // You might want to create a separate method for dispute notifications
            String subject = "Dispute Update - Order #" + order.getOrderNumber();
            String content = "<p>Your dispute for order #" + order.getOrderNumber() + " has been updated.</p>" +
                           "<p><strong>Status:</strong> " + dispute.getStatus() + "</p>" +
                           "<p><strong>Reason:</strong> " + dispute.getReason() + "</p>" +
                           (dispute.getEvidenceDetails() != null ? 
                            "<p><strong>Evidence Due By:</strong> " + dispute.getEvidenceDetails().getDueBy() + "</p>" : "") +
                           "<p>If you have any questions, please contact our support team.</p>";
            
            emailService.sendEmail(
                order.getUser().getEmail(),
                subject,
                content
            );
            log.info("Sent dispute notification email for order {}", order.getId());
        } catch (Exception e) {
            log.error("Failed to send dispute notification email for order {}: {}", 
                     order.getId(), e.getMessage(), e);
        }
    }
}
