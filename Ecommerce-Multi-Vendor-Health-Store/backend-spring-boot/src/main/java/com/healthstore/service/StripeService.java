package com.healthstore.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    public StripeService() {
        // This is where you would set your Stripe API key, but we'll do it in the method
        // to handle the @Value annotation.
    }

    public Session createCheckoutSession(List<SessionCreateParams.LineItem> lineItems, String successUrl, String cancelUrl) throws StripeException {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(lineItems)
                .build();

        return Session.create(params);
    }
}
