package com.intifix.modules.payments.provider;

import org.springframework.stereotype.Component;

@Component
public class MercadoPagoProvider extends AbstractPaymentProvider {

    @Override
    protected String getProviderName() {
        return "MercadoPago";
    }

    @Override
    protected String getTransactionPrefix() {
        return "MP-";
    }
}
