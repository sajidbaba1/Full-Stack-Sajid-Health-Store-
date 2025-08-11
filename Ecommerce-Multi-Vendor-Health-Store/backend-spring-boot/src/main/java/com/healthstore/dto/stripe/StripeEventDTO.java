package com.healthstore.dto.stripe;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO representing a Stripe webhook event.
 * This is a generic class that can hold any type of Stripe event data.
 *
 * @param <T> The type of the data object contained in the event
 */
@Data
public class StripeEventDTO<T> {
    /**
     * Unique identifier for the event.
     */
    private String id;
    
    /**
     * Type of the event (e.g., checkout.session.completed, payment_intent.succeeded, etc.).
     */
    private String type;
    
    /**
     * Time at which the event was created (Unix timestamp).
     */
    private Long created;
    
    /**
     * The data object containing the event details.
     * The actual type depends on the event type.
     */
    private T data;
    
    /**
     * The API version of the event.
     */
    @JsonProperty("api_version")
    private String apiVersion;
    
    /**
     * The ID of the API request that caused the event.
     * This is null for events that weren't created by an API request.
     */
    @JsonProperty("request")
    private String requestId;
    
    /**
     * Information about the API request that caused the event.
     * Only present for events created by an API request.
     */
    @JsonProperty("request_id")
    private String request;
    
    /**
     * The ID of the connected account that originated the event.
     */
    @JsonProperty("account")
    private String accountId;
}
