package com.intifix.modules.payments.provider;

import org.springframework.stereotype.Component;

@Component
public class StripeProvider extends AbstractPaymentProvider {

    @Override
    protected String getProviderName() {
        return "Stripe";
    }

    @Override
    protected String getTransactionPrefix() {
        return "STRIPE-";
    }
}
