package com.intifix.modules.payments.controller;

import com.intifix.modules.payments.dto.CreatePaymentRequest;
import com.intifix.modules.payments.dto.PaymentDto;
import com.intifix.modules.payments.service.PaymentService;
import com.intifix.shared.api.ApiResponse;
import com.intifix.shared.dto.PageRequestDto;
import com.intifix.shared.dto.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ApiResponse<PaymentDto> create(@Valid @RequestBody CreatePaymentRequest req) {
        return ApiResponse.ok(paymentService.create(req));
    }

    @PostMapping("/{pagoId}/confirm")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ApiResponse<PaymentDto> confirm(@PathVariable UUID pagoId) {
        return ApiResponse.ok(paymentService.markPaid(pagoId));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ApiResponse<PageResponse<PaymentDto>> myPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(paymentService.myPayments(new PageRequestDto(page, size)));
    }
}
