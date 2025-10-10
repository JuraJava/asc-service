package com.yurdan.ascService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.dto.PaymentTransactionsRequestDto;
import com.yurdan.ascService.dto.PaymentTransactionsResponseDto;
import com.yurdan.ascService.exception.*;
import com.yurdan.ascService.mapper.PaymentTransactionsMapper;
import com.yurdan.ascService.model.entity.Employee;
import com.yurdan.ascService.model.entity.OutboxEvent;
import com.yurdan.ascService.model.entity.PaymentTransactions;
import com.yurdan.ascService.model.entity.RepairRequest;
import com.yurdan.ascService.model.entity.WorkOrder;
import com.yurdan.ascService.model.enums.OutboxEventStatus;
import com.yurdan.ascService.model.enums.OutboxEventType;
import com.yurdan.ascService.model.enums.PaymentTransactionStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.model.enums.RequestStatus;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.repository.EmployeeRepository;
import com.yurdan.ascService.repository.OutboxEventRepository;
import com.yurdan.ascService.repository.PaymentTransactionsRepository;
import com.yurdan.ascService.repository.RepairRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Класс - сервис, который отвечает за создание и обработку платежных транзакций, связанных с заявками на
 * ремонт. Он проверяет бизнес-логику, сохраняет данные, создаёт события для Outbox-паттерна (отправка событий в Kafka)
 * и возвращает данные о созданной транзакции.
 */
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

    /**
     * Создаёт новую платежную транзакцию.
     * Вся работа происходит в рамках одной транзакции.
     */
    @Transactional
    public PaymentTransactionsResponseDto createPaymentTransactions(PaymentTransactionsRequestDto requestDto) {
        Long repairRequestId = requestDto.getRepairRequestId();

// Проверка наличия платёжной операции по ремонту
        if (paymentTransactionsRepository.existsByRepairRequestId(repairRequestId)) {
            throw new PaymentTransactionAlreadyExistsException(repairRequestId);
        }
// Проверка наличия сотрудника, принявшего заявку
        Employee acceptedBy = employeeRepository.findById(requestDto.getAcceptedById())
                .orElseThrow(() -> new EmployeeNotFoundException(requestDto.getAcceptedById()));
// Проверка наличия заявки на ремонт
        RepairRequest repairRequest = repairRequestRepository.findById(repairRequestId)
                .orElseThrow(() -> new RepairRequestNotFoundException(repairRequestId));
// Проверка типа ремонта
        if (repairRequest.getTypeOfRepair() == TypeOfRepair.WARRANTY) {
            throw new NoPaymentForWarrantyRepairs(repairRequestId);
        }
// Обновление статуса заявки
        repairRequest.setRequestStatus(RequestStatus.ON_PAYMENT);
// Получение и проверка наличия заказов на работы
        List<WorkOrder> workOrders = Optional.ofNullable(repairRequest.getWorkOrders()).orElse(List.of());

        if (workOrders.isEmpty()) {
            throw new NoWorkOrdersForRepairRequestException(repairRequestId);
        }
// Проверка, что все ремонты закрыты
        boolean allClosed = workOrders.stream()
                .allMatch(order -> order.getRepairStatus() == RepairStatus.CLOSED);

        if (!allClosed) {
            throw new NotAllWorkOrdersClosedException(repairRequestId);
        }
// Подсчёт общей суммы
        BigDecimal amount = workOrders.stream()
                .map(WorkOrder::getTotalCost)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
// Создание сущности платежной транзакции
        PaymentTransactions paymentTransaction = PaymentTransactions.builder()
                .transactionStatus(PaymentTransactionStatus.IN_PROCESSING)
                .typeOfPayment(requestDto.getTypeOfPayment())
                .currency(requestDto.getCurrency())
                .amount(amount)
                .repairRequest(repairRequest)
                .acceptedBy(acceptedBy)
                .build();
// Сохранение платёжной операции в БД
        PaymentTransactions savedTransaction = paymentTransactionsRepository.save(paymentTransaction);
// Маппинг и сериализация для Outbox-события
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