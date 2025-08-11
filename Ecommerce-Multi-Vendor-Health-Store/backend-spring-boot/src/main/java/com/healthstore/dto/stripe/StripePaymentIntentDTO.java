package com.healthstore.dto.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

/**
 * DTO representing a Stripe Payment Intent object.
 * This is used in the payment_intent.succeeded and other payment intent webhook events.
 */
@Data
public class StripePaymentIntentDTO {
    /**
     * Unique identifier for the payment intent.
     */
    private String id;
    
    /**
     * The type of object (should be "payment_intent").
     */
    private String object;
    
    /**
     * Amount intended to be collected by this PaymentIntent (in cents).
     */
    private Long amount;
    
    /**
     * Amount that can be captured from the PaymentIntent (in cents).
     */
    @JsonProperty("amount_capturable")
    private Long amountCapturable;
    
    /**
     * Amount that was collected by this PaymentIntent (in cents).
     */
    @JsonProperty("amount_received")
    private Long amountReceived;
    
    /**
     * The client secret of this PaymentIntent.
     */
    @JsonProperty("client_secret")
    private String clientSecret;
    
    /**
     * Three-letter ISO currency code, in lowercase.
     */
    private String currency;
    
    /**
     * ID of the Customer this PaymentIntent belongs to, if one exists.
     */
    private String customer;
    
    /**
     * An arbitrary string attached to the object.
     */
    private String description;
    
    /**
     * The payment error encountered in the previous PaymentIntent confirmation.
     */
    @JsonProperty("last_payment_error")
    private Object lastPaymentError;
    
    /**
     * Has the value `true` if the object exists in live mode or the value `false` if the object exists in test mode.
     */
    private boolean livemode;
    
    /**
     * Set of key-value pairs that you can attach to the payment intent.
     */
    private Map<String, String> metadata;
    
    /**
     * If present, this property tells you what actions you need to take in order for your customer to fulfill a payment using the provided source.
     */
    @JsonProperty("next_action")
    private Object nextAction;
    
    /**
     * The account (if any) for which the funds of the PaymentIntent are intended.
     */
    @JsonProperty("on_behalf_of")
    private String onBehalfOf;
    
    /**
     * ID of the payment method used in this PaymentIntent.
     */
    @JsonProperty("payment_method")
    private String paymentMethod;
    
    /**
     * Payment-method-specific configuration for this PaymentIntent.
     */
    @JsonProperty("payment_method_options")
    private Object paymentMethodOptions;
    
    /**
     * The list of payment method types (e.g. card) that this PaymentIntent is allowed to use.
     */
    @JsonProperty("payment_method_types")
    private String[] paymentMethodTypes;
    
    /**
     * Email address that the receipt for the resulting payment will be sent to.
     */
    @JsonProperty("receipt_email")
    private String receiptEmail;
    
    /**
     * ID of the review associated with this PaymentIntent, if any.
     */
    private String review;
    
    /**
     * Indicates that you intend to make future payments with this PaymentIntent's payment method.
     */
    @JsonProperty("setup_future_usage")
    private String setupFutureUsage;
    
    /**
     * Shipping information for this PaymentIntent.
     */
    private Object shipping;
    
    /**
     * This is a legacy field that will be removed in the future.
     */
    private Object source;
    
    /**
     * Extra information about a PaymentIntent.
     */
    private Object statementDescriptor;
    
    /**
     * Provides information about a card payment that customers see on their statements.
     */
    @JsonProperty("statement_descriptor_suffix")
    private String statementDescriptorSuffix;
    
    /**
     * Status of this PaymentIntent.
     */
    private String status;
    
    /**
     * The data with which to automatically create a Transfer when the payment is finalized.
     */
    @JsonProperty("transfer_data")
    private Object transferData;
    
    /**
     * A string that identifies the resulting payment as part of a group.
     */
    @JsonProperty("transfer_group")
    private String transferGroup;
    
    /**
     * The payment method type that was used for this payment.
     */
    @JsonProperty("payment_method_type")
    private String paymentMethodType;
    
    /**
     * The date and time when the payment intent was created (Unix timestamp).
     */
    private Long created;
}
