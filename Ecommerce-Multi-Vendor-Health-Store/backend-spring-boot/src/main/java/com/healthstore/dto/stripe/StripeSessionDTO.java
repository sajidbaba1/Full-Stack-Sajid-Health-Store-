package com.healthstore.dto.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * DTO representing a Stripe Checkout Session object.
 * This is used in the checkout.session.completed webhook event.
 */
@Data
public class StripeSessionDTO {
    /**
     * Unique identifier for the session.
     */
    private String id;
    
    /**
     * The type of object (should be "checkout.session").
     */
    private String object;
    
    /**
     * The client reference ID set during session creation.
     * This typically contains the order ID from our system.
     */
    @JsonProperty("client_reference_id")
    private String clientReferenceId;
    
    /**
     * The status of the payment (e.g., "paid", "unpaid", "no_payment_required").
     */
    @JsonProperty("payment_status")
    private String paymentStatus;
    
    /**
     * The status of the checkout session (e.g., "complete", "expired", "open").
     */
    private String status;
    
    /**
     * The mode of the Checkout Session (e.g., "payment", "setup", "subscription").
     */
    private String mode;
    
    /**
     * The email address provided by the customer during checkout.
     */
    @JsonProperty("customer_email")
    private String customerEmail;
    
    /**
     * The ID of the customer this checkout is for, if one exists.
     */
    @JsonProperty("customer")
    private String customerId;
    
    /**
     * The URL the customer will be directed to after the payment or subscription creation is processed.
     */
    @JsonProperty("success_url")
    private String successUrl;
    
    /**
     * The URL the customer will be directed to if they decide to cancel payment.
     */
    @JsonProperty("cancel_url")
    private String cancelUrl;
    
    /**
     * The amount total of the session in the smallest currency unit.
     */
    @JsonProperty("amount_total")
    private Long amountTotal;
    
    /**
     * The currency of the session (e.g., "usd").
     */
    private String currency;
    
    /**
     * Custom fields that were set during session creation.
     */
    @JsonProperty("custom_fields")
    private Object customFields;
    
    /**
     * The line items purchased.
     */
    @JsonProperty("line_items")
    private Object lineItems;
    
    /**
     * The payment intent ID associated with this session, if any.
     */
    @JsonProperty("payment_intent")
    private String paymentIntentId;
    
    /**
     * The subscription ID associated with this session, if any.
     */
    @JsonProperty("subscription")
    private String subscriptionId;
    
    /**
     * Set of key-value pairs that you can attach to the session.
     */
    private Map<String, String> metadata;
    
    /**
     * The timestamp when the session was created (Unix timestamp).
     */
    private Long created;
    
    /**
     * The timestamp when the session will expire (Unix timestamp).
     */
    private Long expiresAt;
    
    /**
     * The URL to the Checkout Session.
     */
    private String url;
}
