package com.yurdan.ascService.controller.external.work_order_controller;

import com.yurdan.ascService.IntegrationContext;
import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.model.enums.PaymentStatus;
import com.yurdan.ascService.model.enums.RepairStatus;
import com.yurdan.ascService.repository.WorkOrderRepository;
import com.yurdan.ascService.security.JwtAuthentication;
import com.yurdan.ascService.security.JwtUtils;
import com.yurdan.ascService.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * До запуска каждого теста загружается предустановленный SQL-скрипт,
 * создающий тестовые данные.
 * После теста БД очищается.
 */
@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        value = "/scripts/controller/external/work_order_controller/CreateWorkOrder.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD,
        value = "/scripts/db/clear.sql")
/**
 * Этот класс CreateWorkOrderIntegrationTest представляет собой интеграционный тест для REST-контроллера create-work-order,
 * который создаёт заказ-наряд (WorkOrder) на ремонт техники.
 */
public class CreateWorkOrderIntegrationTest extends IntegrationContext {

    /**
     * Статический токен авторизации.
     * Статический UUID пользователя (используется в JWT)
     */
    private static final String ACCESS_TOKEN = "Bearer token";
    private static final UUID USER_ID = UUID.fromString("0b853e91-0768-48e0-8905-e5a512db65dd");
    /**
     * Настоящие бины, но можно следить за вызовами методов (verify).
     */
    @SpyBean
    private WorkOrderService workOrderService;

    @SpyBean
    private WorkOrderRepository workOrderRepository;

    /**
     * Полностью поддельный бин, нужен для эмуляции JWT-аутентификации.
     */
    @MockBean
    private JwtUtils jwtUtils;

    /**
     * Создаёт объект JwtAuthentication
     */
    private JwtAuthentication setupJwtAuthentication(UUID userId, List<String> roles) {
        JwtAuthentication jwtAuthentication = new JwtAuthentication(
                userId,
                roles.stream().map(SimpleGrantedAuthority::new).toList()
        );
        jwtAuthentication.setAuthenticated(true);
        return jwtAuthentication;
    }

    /**
     * Имитация поведения JWT:
     * validateToken(...) всегда возвращает true.
     * getAuthentication(...) возвращает поддельного пользователя.
     */
    private void setupJwtMocks(JwtAuthentication authentication) {
        when(jwtUtils.validateToken(any(String.class))).thenReturn(true);
        when(jwtUtils.getAuthentication(any(String.class))).thenReturn(authentication);
    }

    /**
     * Тест успешного создания заказ-наряда.
     * Эмулирует ENGINEER-пользователя.
     * Формирует запрос с валидными ID для:
     * заявки на ремонт repairRequestId = 2
     * запасных частей sparePartIds = [10, 11]
     * выполненных работ completedWorkIds = [3, 4]
     * Выполняет POST-запрос /asc/create-work-order.
     * Проверяет статус 200 OK.
     * Проверяет структуру JSON-ответа: поля, вложенные массивы, значения, сумма стоимости,
     * и т.д. — через checkSuccessResponse(result).
     */
    @Test
    void createWorkOrder_success() throws Exception {
        // given
        JwtAuthentication authentication = setupJwtAuthentication(USER_ID, List.of("ENGINEER"));
        setupJwtMocks(authentication);

        CreateWorkOrderDto request = CreateWorkOrderDto.builder()
                .repairRequestId(2L) // <-- это значение должно существовать в БД
                .sparePartIds(List.of(10L, 11L))
                .completedWorkIds(List.of(3L, 4L))
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/asc/create-work-order")
                        .param("performedById", "8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", ACCESS_TOKEN)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpect(status().isOk());

        // then
        checkSuccessResponse(result);

        verify(workOrderService, times(1)).createWorkOrder(any(CreateWorkOrderDto.class), eq(8L));
        verify(workOrderRepository, times(1)).save(any());
        verifyNoMoreInteractions(workOrderRepository);
    }

    /**
     * Проверка поведения без валидного токена.
     * JWT валиден: false.
     * Отправляется пустой CreateWorkOrderDto.
     * Проверяется:
     * Статус 401 Unauthorized.
     * Пустое тело ответа.
     * Тип контента не null.
     */
    @Test
    void createWorkOrder_unauthorized() throws Exception {
        // given
        when(jwtUtils.validateToken(any(String.class))).thenReturn(false);

        // when / then
        mockMvc.perform(
                        post("/asc/create-work-order")
                                .param("performedById", "8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", ACCESS_TOKEN)
                                .content(objectMapper.writeValueAsString(new CreateWorkOrderDto()))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(result -> {
                    String contentType = result.getResponse().getContentType();
                    assertNotNull(contentType, "Content type not set"); // <--- текущая ошибка
                })
//                .andExpect(jsonPath("$.error").value("Unauthorized access"));
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("")); // тело пустое
        verifyNoInteractions(workOrderService, workOrderRepository);

        verifyNoMoreInteractions(workOrderService, workOrderRepository);
    }

    /**
     * Очень подробная проверка успешного JSON-ответа
     */
    private void checkSuccessResponse(ResultActions actions) throws Exception {
        actions
                // id может отличаться — проверим, что он есть и числовой
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.repairStatus").value(RepairStatus.AT_WORK.name()))
                .andExpect(jsonPath("$.paymentStatus").value(PaymentStatus.UNPAID.name()))

                // spare parts
                .andExpect(jsonPath("$.spareParts").isArray())
                .andExpect(jsonPath("$.spareParts.length()").value(2))
                .andExpect(jsonPath("$.spareParts[0].id").value(11))
                .andExpect(jsonPath("$.spareParts[0].batchNumber").value("GH82-265849B"))
                .andExpect(jsonPath("$.spareParts[0].remainingQuantity").value(2))
                .andExpect(jsonPath("$.spareParts[0].reserveQuantity").value(1))
                .andExpect(jsonPath("$.spareParts[0].cost").value(742.47))
                .andExpect(jsonPath("$.spareParts[1].id").value(10))
                .andExpect(jsonPath("$.spareParts[1].batchNumber").value("GH82-173072B"))
                .andExpect(jsonPath("$.spareParts[1].remainingQuantity").value(3))
                .andExpect(jsonPath("$.spareParts[1].reserveQuantity").value(1))
                .andExpect(jsonPath("$.spareParts[1].cost").value(140.72))

                // completed works
                .andExpect(jsonPath("$.completedWorks").isArray())
                .andExpect(jsonPath("$.completedWorks.length()").value(2))
                .andExpect(jsonPath("$.completedWorks[0].id").value(3))
                .andExpect(jsonPath("$.completedWorks[0].nameWork").value("REPLACEMENT_ON_PAID_BASIS"))
                .andExpect(jsonPath("$.completedWorks[0].cost").value(3500.00))
                .andExpect(jsonPath("$.completedWorks[1].id").value(4))
                .andExpect(jsonPath("$.completedWorks[1].nameWork").value("ADDITIONAL_WORK"))
                .andExpect(jsonPath("$.completedWorks[1].cost").value(4500.00))

                // totals
                .andExpect(jsonPath("$.costOfSparePart").value(883.19))
                .andExpect(jsonPath("$.costOfWork").value(8000.00))
                .andExpect(jsonPath("$.totalCost").value(8883.19))
                // точное сравнение BigDecimal с плавающей запятой может падать — если будет проблема, уберите или округляйте
                .andExpect(jsonPath("$.salary").value(3200.000047683716))

                // performer
                .andExpect(jsonPath("$.performedBy.id").value(8))
                .andExpect(jsonPath("$.performedBy.fullName").value("Тен Александр"))
                .andExpect(jsonPath("$.performedBy.role").value("ENGINEER"))

                .andExpect(jsonPath("$.repairRequestId").value(2));
    }

    /**
     * Случай, когда указанный repairRequestId отсутствует в БД.
     * Отправляется запрос с несуществующим ID 999L.
     * Проверяется:
     * Статус 404 Not Found.
     * Сообщение об ошибке в теле ответа.
     */
    @Test
    void createWorkOrder_repairRequestNotFound_shouldReturnNotFound() throws Exception {
        // given
        JwtAuthentication authentication = setupJwtAuthentication(USER_ID, List.of("ENGINEER"));
        setupJwtMocks(authentication);

        CreateWorkOrderDto request = CreateWorkOrderDto.builder()
                .repairRequestId(999L) // <-- ID, которого нет в БД
                .sparePartIds(List.of(10L, 11L))
                .completedWorkIds(List.of(3L, 4L))
                .build();

        // when / then
        mockMvc.perform(
                        post("/asc/create-work-order")
                                .param("performedById", "8")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", ACCESS_TOKEN)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Repair request not found with ID: 999"));

    }
}