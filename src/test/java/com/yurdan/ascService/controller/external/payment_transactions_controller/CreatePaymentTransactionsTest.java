package com.yurdan.ascService.controller.external.payment_transactions_controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.yurdan.ascService.IntegrationContext;
import com.yurdan.ascService.dto.PaymentTransactionsRequestDto;
import com.yurdan.ascService.dto.PaymentTransactionsResponseDto;
import com.yurdan.ascService.mapper.PaymentTransactionsMapper;
import com.yurdan.ascService.model.entity.Employee;
import com.yurdan.ascService.model.entity.PaymentTransactions;
import com.yurdan.ascService.model.entity.RepairRequest;
import com.yurdan.ascService.model.entity.WorkOrder;
import com.yurdan.ascService.model.enums.Currencies;
import com.yurdan.ascService.model.enums.PaymentTransactionStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.model.enums.RequestStatus;
import com.yurdan.ascService.model.enums.RoleOfEmployee;
import com.yurdan.ascService.model.enums.TypeOfPayment;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.repository.*;
import com.yurdan.ascService.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * До запуска каждого теста загружается предустановленный SQL-скрипт,
 * создающий тестовые данные.
 * После теста БД очищается.
 */
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        value = "/scripts/controller/external/payment_transactions_controller/CreatePaymentTransactions.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        value = "/scripts/db/clear.sql")

/**
 * Загружает весь Spring-контекст.
 * Автоматически настраивает MockMvc — инструмент для тестирования REST-контроллеров без запуска сервера.
 */
@SpringBootTest
@AutoConfigureMockMvc

/**
 * Этот тестовый класс предназначен для интеграционного тестирования REST-контроллера, обрабатывающего создание
 * платёжных транзакций в приложении, связанном в ASC — авторизованном сервисном центре.
 * Тесты выполняются в контексте Spring Boot с использованием MockMvc.
 */
class CreatePaymentTransactionsTest extends IntegrationContext {

    /**
     * для имитации HTTP-запросов
     */
    @Autowired
    private MockMvc mockMvc;
    /**
     * заменяет настоящие бины моками
     */
    @MockBean
    private PaymentTransactionsRepository paymentTransactionsRepository;

    @MockBean
    private RepairRequestRepository repairRequestRepository;

    @MockBean
    private EmployeeRepository employeeRepository;

    @MockBean
    private OutboxEventRepository outboxEventRepository;

    @MockBean
    private PaymentTransactionsMapper paymentTransactionsMapper;

    @MockBean
    private JwtUtils jwtUtils;
    /**
     * для сериализации/десериализации JSON
     */
    @Autowired
    private ObjectMapper objectMapper;

    private RepairRequest repairRequest;
    private List<WorkOrder> closedWorkOrders;
    private Employee acceptedBy;
    private PaymentTransactions savedTransaction;
    private PaymentTransactionsResponseDto responseDto;

    /**
     * Вызывается перед каждым тестом.
     * Создаёт:
     * сотрудника (acceptedBy) с ролью RECEIVER;
     * 2 завершённых заказа (closedWorkOrders);
     * заявку на ремонт (repairRequest) со статусом ON_PAYMENT, в которую включены вышеуказанные заказы;
     * сохранённую платёжную транзакцию (savedTransaction);
     * DTO-объект ответа (responseDto).
     */
    @BeforeEach
    void setup() {
        // Сотрудник
        acceptedBy = Employee.builder()
                .id(7L)
                .role(RoleOfEmployee.RECEIVER)
                .build();

        // WorkOrders закрыты
        WorkOrder order1 = WorkOrder.builder()
                .id(5L)
                .repairStatus(RepairStatus.CLOSED)
                .totalCost(new BigDecimal("39766.25"))
                .build();

        WorkOrder order2 = WorkOrder.builder()
                .id(6L)
                .repairStatus(RepairStatus.CLOSED)
                .totalCost(new BigDecimal("8883.19"))
                .build();

        closedWorkOrders = List.of(order1, order2);

        // Заявка на ремонт
        repairRequest = RepairRequest.builder()
                .id(3L)
                .typeOfRepair(TypeOfRepair.NON_WARRANTY)
                .requestStatus(RequestStatus.ON_PAYMENT)
                .workOrders(closedWorkOrders)
                .build();

        // Платёжная транзакция
        savedTransaction = PaymentTransactions.builder()
                .id(2L)
                .transactionStatus(PaymentTransactionStatus.IN_PROCESSING)
                .currency(Currencies.RUB)
                .typeOfPayment(TypeOfPayment.NON_CASH)
                .repairRequest(repairRequest)
                .acceptedBy(acceptedBy)
                .amount(new BigDecimal("48649.44"))
                .build();

        // DTO для ответа
        responseDto = PaymentTransactionsResponseDto.builder()
                .id(2L)
                .transactionStatus(PaymentTransactionStatus.IN_PROCESSING)
                .typeOfPayment(TypeOfPayment.NON_CASH)
                .currency(Currencies.RUB)
                .repairRequestId(3L)
                .acceptedBy(7L)
                .executedAt(LocalDateTime.now())
                .amount(new BigDecimal("48649.44"))
                .build();
    }

    /**
     * Проверяет успешное создание платёжной транзакции.
     * Мокает зависимости:
     * Транзакции по этой заявке ещё нет.
     * Сотрудник и заявка существуют.
     * После сохранения возвращается savedTransaction.
     * Маппер превращает сущность в responseDto.
     * Выполняет HTTP POST:
     * POST /asc/payment-transactions/payment-transaction
     * Authorization: mock user with role RECEIVER
     * Проверяет:
     * код ответа: 200 OK;
     * JSON-ответ содержит нужные поля и значения: ID, тип оплаты, валюта, сумма, кто принял и т. д.
     */
    @Test
    @WithMockUser(authorities = {"RECEIVER"})
    void shouldCreatePaymentTransactionSuccessfully() throws Exception {
        // DTO запроса
        PaymentTransactionsRequestDto requestDto = PaymentTransactionsRequestDto.builder()
                .repairRequestId(3L)
                .typeOfPayment(TypeOfPayment.NON_CASH)
                .currency(Currencies.RUB)
                .acceptedById(7L)
                .build();

        // Мокаем зависимости
        when(paymentTransactionsRepository.existsByRepairRequestId(3L)).thenReturn(false);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(acceptedBy));
        when(repairRequestRepository.findById(3L)).thenReturn(Optional.of(repairRequest));
        when(paymentTransactionsRepository.save(any())).thenReturn(savedTransaction);
        when(paymentTransactionsMapper.toDto(savedTransaction)).thenReturn(responseDto);

        mockMvc.perform(post("/asc/payment-transactions/payment-transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.repairRequestId").value(3))
                .andExpect(jsonPath("$.typeOfPayment").value("NON_CASH"))
                .andExpect(jsonPath("$.transactionStatus").value("IN_PROCESSING"))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.amount").value(48649.44))
                .andExpect(jsonPath("$.acceptedBy").value(7));
    }

    /**
     * Цель: отказать в доступе, если пользователь не RECEIVER.
     * Пользователь с ролью ENGINEER.
     * Выполняется POST-запрос.
     * Ожидается 403 Forbidden
     */
    @Test
    @WithMockUser(authorities = {"ENGINEER"})
    void shouldReturn403WhenUserHasInvalidRole() throws Exception {
        PaymentTransactionsRequestDto requestDto = PaymentTransactionsRequestDto.builder()
                .repairRequestId(3L)
                .typeOfPayment(TypeOfPayment.NON_CASH)
                .currency(Currencies.RUB)
                .acceptedById(8L)
                .build();

        mockMvc.perform(post("/asc/payment-transactions/payment-transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isForbidden());
    }

    /**
     * Цель: отказать, если транзакция уже существует.
     * Мокает paymentTransactionsRepository.existsByRepairRequestId(3L) → true.
     * Ожидается:
     * 400 Bad Request;
     * JSON-ошибка: "Платёжная операция по заявке с ID 3 уже существует."
     */
    @Test
    @WithMockUser(authorities = {"RECEIVER"})
    void shouldReturn400IfTransactionAlreadyExists() throws Exception {
        when(paymentTransactionsRepository.existsByRepairRequestId(3L)).thenReturn(true);

        PaymentTransactionsRequestDto requestDto = PaymentTransactionsRequestDto.builder()
                .repairRequestId(3L)
                .typeOfPayment(TypeOfPayment.NON_CASH)
                .currency(Currencies.RUB)
                .acceptedById(7L)
                .build();

        mockMvc.perform(post("/asc/payment-transactions/payment-transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Платёжная операция по заявке с ID 3 уже существует."));
    }

    /**
     * Цель: ошибка, если заявка на ремонт не найдена.
     * Репозиторий repairRequestRepository.findById(3L) возвращает Optional.empty().
     * Ожидается:
     * 404 Not Found;
     * сообщение: "Repair request not found with ID: 3".
     */
    @Test
    @WithMockUser(authorities = {"RECEIVER"})
    void shouldReturn404IfRepairRequestNotFound() throws Exception {
        when(paymentTransactionsRepository.existsByRepairRequestId(3L)).thenReturn(false);
        when(employeeRepository.findById(7L)).thenReturn(Optional.of(acceptedBy));
        when(repairRequestRepository.findById(3L)).thenReturn(Optional.empty());

        PaymentTransactionsRequestDto requestDto = PaymentTransactionsRequestDto.builder()
                .repairRequestId(3L)
                .typeOfPayment(TypeOfPayment.NON_CASH)
                .currency(Currencies.RUB)
                .acceptedById(7L)
                .build();

        mockMvc.perform(post("/asc/payment-transactions/payment-transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Repair request not found with ID: 3"));
    }
}