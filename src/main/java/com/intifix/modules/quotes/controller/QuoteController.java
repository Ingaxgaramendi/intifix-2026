package com.intifix.modules.quotes.controller;

import com.intifix.modules.quotes.dto.CreateQuoteRequest;
import com.intifix.modules.quotes.dto.QuoteDto;
import com.intifix.modules.quotes.service.QuoteService;
import com.intifix.shared.api.ApiResponse;
import com.intifix.shared.dto.PageRequestDto;
import com.intifix.shared.dto.PageResponse;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/quotes")
public class QuoteController {

    private final QuoteService quoteService;

    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @PostMapping
    @PreAuthorize("hasRole('TECNICO')")
    public ApiResponse<QuoteDto> submit(@Valid @RequestBody CreateQuoteRequest req) {
        return ApiResponse.ok(quoteService.submit(req));
    }

    @GetMapping("/service/{servicioId}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ApiResponse<PageResponse<QuoteDto>> listForService(
            @PathVariable UUID servicioId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ApiResponse.ok(quoteService.listForService(servicioId, new PageRequestDto(page, size)));
    }
}
