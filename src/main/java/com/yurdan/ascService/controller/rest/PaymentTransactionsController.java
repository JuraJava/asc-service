package com.yurdan.ascService.controller.rest;

import com.yurdan.ascService.dto.*;
import com.yurdan.ascService.service.PaymentTransactionsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Этот класс  — это REST-контроллер, который предоставляет API для создания платежных транзакций.
 */
@RestController
@RequestMapping("/asc/payment-transactions")
public class PaymentTransactionsController {

    private final PaymentTransactionsService transactionsService;

    public PaymentTransactionsController(PaymentTransactionsService transactionsService) {
        this.transactionsService = transactionsService;
    }

    /**
     * Метод обрабатывает POST-запрос по пути:
     * /asc/payment-transactions/payment-transaction.
     * Разрешён доступ только пользователям с правами RECEIVER или ADMINISTRATOR,
     * используется при включённой Spring Security,
     * метод  защищён авторизацией.
     */
    @PreAuthorize("hasAnyAuthority('RECEIVER', 'ADMINISTRATOR')")
    @PostMapping("/payment-transaction")
    public ResponseEntity<PaymentTransactionsResponseDto> createPaymentTransactions(
            @Valid @RequestBody PaymentTransactionsRequestDto requestDto) {
        // Уже получаем DTO
        PaymentTransactionsResponseDto responseDto = transactionsService.createPaymentTransactions(requestDto);
        return ResponseEntity.ok(responseDto);
    }
}