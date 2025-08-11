package com.healthstore.service;

import com.healthstore.model.Order;

/**
 * Service for sending email notifications.
 */
public interface EmailService {
    
    /**
     * Sends an order confirmation email to the customer.
     *
     * @param order The order for which to send the confirmation
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendOrderConfirmation(Order order);
    
    /**
     * Sends a payment confirmation email to the customer.
     *
     * @param order The order for which the payment was received
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendPaymentConfirmation(Order order);
    
    /**
     * Sends a shipping confirmation email to the customer.
     *
     * @param order The order that has been shipped
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendShippingConfirmation(Order order);
    
    /**
     * Sends a refund confirmation email to the customer.
     *
     * @param order The order for which a refund was processed
     * @param refundAmount The amount that was refunded
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendRefundConfirmation(Order order, double refundAmount);
    
    /**
     * Sends a payment failure notification to the customer.
     *
     * @param order The order for which payment failed
     * @param errorMessage The error message from the payment processor
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendPaymentFailureNotification(Order order, String errorMessage);
    
    /**
     * Sends a generic notification email.
     *
     * @param toEmail The recipient's email address
     * @param subject The email subject
     * @param content The email content (can be HTML)
     * @return true if the email was sent successfully, false otherwise
     */
    boolean sendEmail(String toEmail, String subject, String content);
}
