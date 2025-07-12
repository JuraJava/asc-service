package com.yurdan.ascService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.dto.PaymentTransactionsRequestDto;
import com.yurdan.ascService.dto.PaymentTransactionsResponseDto;
import com.yurdan.ascService.exception.*;
import com.yurdan.ascService.mapper.PaymentTransactionsMapper;
import com.yurdan.ascService.model.entity.*;
import com.yurdan.ascService.model.enums.*;
import com.yurdan.ascService.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class PaymentTransactionsService {

    private final PaymentTransactionsRepository paymentTransactionsRepository;
    private final RepairRequestRepository repairRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final PaymentTransactionsMapper paymentTransactionsMapper;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public PaymentTransactionsService(
            RepairRequestRepository repairRequestRepository,
            PaymentTransactionsRepository paymentTransactionsRepository,
            EmployeeRepository employeeRepository,
            PaymentTransactionsMapper paymentTransactionsMapper,
            OutboxEventRepository outboxEventRepository,
            ObjectMapper objectMapper
    ) {
        this.repairRequestRepository = repairRequestRepository;
        this.paymentTransactionsRepository = paymentTransactionsRepository;
        this.employeeRepository = employeeRepository;
        this.paymentTransactionsMapper = paymentTransactionsMapper;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentTransactionsResponseDto createPaymentTransactions(PaymentTransactionsRequestDto requestDto) {
        Long repairRequestId = requestDto.getRepairRequestId();

        if (paymentTransactionsRepository.existsByRepairRequestId(repairRequestId)) {
            throw new PaymentTransactionAlreadyExistsException(repairRequestId);
        }

        Employee acceptedBy = employeeRepository.findById(requestDto.getAcceptedById())
                .orElseThrow(() -> new EmployeeNotFoundException(requestDto.getAcceptedById()));

        RepairRequest repairRequest = repairRequestRepository.findById(repairRequestId)
                .orElseThrow(() -> new RepairRequestNotFoundException(repairRequestId));

        if (repairRequest.getTypeOfRepair() == TypeOfRepair.WARRANTY) {
            throw new NoPaymentForWarrantyRepairs(repairRequestId);
        }

        repairRequest.setRequestStatus(RequestStatus.ON_PAYMENT);

        List<WorkOrder> workOrders = Optional.ofNullable(repairRequest.getWorkOrders()).orElse(List.of());

        if (workOrders.isEmpty()) {
            throw new NoWorkOrdersForRepairRequestException(repairRequestId);
        }

        boolean allClosed = workOrders.stream()
                .allMatch(order -> order.getRepairStatus() == RepairStatus.CLOSED);

        if (!allClosed) {
            throw new NotAllWorkOrdersClosedException(repairRequestId);
        }

        BigDecimal amount = workOrders.stream()
                .map(WorkOrder::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PaymentTransactions paymentTransaction = PaymentTransactions.builder()
                .transactionStatus(PaymentTransactionStatus.IN_PROCESSING)
                .typeOfPayment(requestDto.getTypeOfPayment())
                .currency(requestDto.getCurrency())
                .amount(amount)
                .repairRequest(repairRequest)
                .acceptedBy(acceptedBy)
                .build();

        PaymentTransactions savedTransaction = paymentTransactionsRepository.save(paymentTransaction);

        // Маппим в DTO перед сериализацией
        PaymentTransactionsResponseDto responseDto = paymentTransactionsMapper.toDto(savedTransaction);

        // Сериализуем DTO, а не сущность
        try {
            String payload = objectMapper.writeValueAsString(responseDto);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateType("PaymentTransaction")
                    .aggregateId(savedTransaction.getId().toString())
                    .eventType(OutboxEventType.PAYMENT_CREATED)
                    .payload(payload)
                    .status(OutboxEventStatus.READY)
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(event);
        } catch (JsonProcessingException e) {
            log.error("Ошибка сериализации PaymentTransaction в JSON", e);
            throw new RuntimeException("Ошибка сериализации события Outbox", e);
        }

        return responseDto;
    }
}