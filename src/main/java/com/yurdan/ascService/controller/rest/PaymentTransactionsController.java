package com.yurdan.ascService.controller.rest;

import com.yurdan.ascService.dto.*;
import com.yurdan.ascService.service.PaymentTransactionsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/asc/payment-transactions")
public class PaymentTransactionsController {

    private final PaymentTransactionsService transactionsService;

    public PaymentTransactionsController(PaymentTransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    @PreAuthorize("hasAnyAuthority('RECEIVER', 'ADMINISTRATOR')")
    @PostMapping("/payment-transaction")
    public ResponseEntity<PaymentTransactionsResponseDto> createPaymentTransactions(
            @Valid @RequestBody PaymentTransactionsRequestDto requestDto) {
        // 🟢 Уже получаем DTO
        PaymentTransactionsResponseDto responseDto = transactionsService.createPaymentTransactions(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}