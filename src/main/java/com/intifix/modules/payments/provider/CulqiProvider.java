package com.intifix.modules.payments.provider;

import org.springframework.stereotype.Component;

@Component
public class CulqiProvider extends AbstractPaymentProvider {

    @Override
    protected String getProviderName() {
        return "Culqi";
    }

    @Override
    protected String getTransactionPrefix() {
        return "CULQI-";
    }
}
