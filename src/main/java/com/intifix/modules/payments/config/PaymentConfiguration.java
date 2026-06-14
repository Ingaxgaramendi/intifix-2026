package com.intifix.modules.payments.config;

import com.intifix.modules.payments.provider.PaymentProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PaymentConfiguration {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "payment.provider.default", havingValue = "mercadopago", matchIfMissing = true)
    public PaymentProvider mercadoPagoProvider(com.intifix.modules.payments.provider.MercadoPagoProvider mercadoPagoProvider) {
        return mercadoPagoProvider;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "payment.provider.default", havingValue = "stripe")
    public PaymentProvider stripeProvider(com.intifix.modules.payments.provider.StripeProvider stripeProvider) {
        return stripeProvider;
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "payment.provider.default", havingValue = "culqi")
    public PaymentProvider culqiProvider(com.intifix.modules.payments.provider.CulqiProvider culqiProvider) {
        return culqiProvider;
    }
}
