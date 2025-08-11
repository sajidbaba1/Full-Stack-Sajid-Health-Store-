package com.healthstore.service;

import com.stripe.model.Charge;
import com.stripe.model.Dispute;
import com.stripe.model.PaymentIntent;

/**
 * Service interface for handling payment-related operations.
 * This service processes payment events from Stripe and updates the application state accordingly.
 */
public interface PaymentService {
    
    /**
     * Handles a successful payment.
     * This method is called when a payment intent succeeds.
     *
     * @param paymentIntent The Stripe PaymentIntent object
     */
    void handleSuccessfulPayment(PaymentIntent paymentIntent);
    
    /**
     * Handles a failed payment.
     * This method is called when a payment intent fails.
     *
     * @param paymentIntent The Stripe PaymentIntent object
     */
    void handleFailedPayment(PaymentIntent paymentIntent);
    
    /**
     * Handles a refund.
     * This method is called when a charge is refunded.
     *
     * @param charge The Stripe Charge object that was refunded
     */
    void handleRefund(Charge charge);
    
    /**
     * Handles a payment dispute.
     * This method is called when a customer disputes a charge.
     *
     * @param dispute The Stripe Dispute object
     */
    void handleDispute(Dispute dispute);
}
