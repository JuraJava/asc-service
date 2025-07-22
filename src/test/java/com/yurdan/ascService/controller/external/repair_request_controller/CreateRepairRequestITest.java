package com.yurdan.ascService.controller.external.repair_request_controller;

import com.yurdan.ascService.IntegrationContext;
import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.model.entity.RepairRequest;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.repository.RepairRequestRepository;
import com.yurdan.ascService.security.JwtAuthentication;
import com.yurdan.ascService.security.JwtUtils;
import com.yurdan.ascService.service.ServiceAscService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD,
        value = "/scripts/controller/external/repair_request_controller/CreateRepairRequest.sql")
@Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/db/clear.sql")
public class CreateRepairRequestITest extends IntegrationContext {

    private static final String accessToken = "Bearer token";
    private final UUID userId = UUID.fromString("28fc7f1b-1b32-44f0-9ec9-66b0c1fdc392");

    @Value("${service_center.name_of_center}")
    private String nameOfCenter;
    @Value("${service_center.phone_number}")
    private String phoneNumber;
    @Value("${service_center.address}")
    private String address;

    @SpyBean
    private ServiceAscService serviceAscService;
    @SpyBean
    private RepairRequestRepository repairRequestRepository;

    @MockBean
    private JwtUtils jwtUtils;

    private final String serialNumber = "SN123456";
    private final String defect = "Defect example";
    private final String appearance = "Good";
    private final String customerFullName = "Ivan Ivanov";
    private final String customerPatronymic = "Ivanovich";
    private final String customerAddress = "Some address";
    private final String customerPhone = "89998887766";
    private final String device = "iPhone 4s BLACK";
    private final Long deviceId = 101L;
    private final String employeeFullName = "Приемова Приемщица";
    private LocalDate saleDate;

    private JwtAuthentication setupJwtAuthentication(UUID userId, List<String> roles) {
        JwtAuthentication jwtAuthentication = new JwtAuthentication(userId, roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList());
        jwtAuthentication.setAuthenticated(true);
        return jwtAuthentication;
    }

    private void setupJwtMocks(JwtAuthentication authentication) {
        when(jwtUtils.validateToken(any(String.class))).thenReturn(true);
        when(jwtUtils.getAuthentication(any(String.class))).thenReturn(authentication);
    }

    @Test
    void createRepairRequest_success() throws Exception {
        JwtAuthentication authentication = setupJwtAuthentication(userId, List.of("RECEIVER"));
        setupJwtMocks(authentication);

        RepairRequestDto request = RepairRequestDto.builder()
                .deviceId(deviceId)
                .typeOfRepair(TypeOfRepair.WARRANTY)
                .serialNumber(serialNumber)
                .saleDate(saleDate = LocalDate.now().minusDays(30))
                .defect(defect)
                .appearance(appearance)
                .customerFullName(customerFullName)
                .customerPatronymic(customerPatronymic)
                .customerAddress(customerAddress)
                .customerPhone(customerPhone)
                .acceptedById(1L)
                .build();

        ResultActions resultActions = mockMvc.perform(
                        post("/asc/repair-request/create-repair-request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", accessToken)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        checkResponse(resultActions);

        verify(repairRequestRepository, times(1)).findBySerialNumber(any(String.class));
        verify(repairRequestRepository, times(1)).save(any(RepairRequest.class));
    }

    @Test
    void createRepairRequest_alreadyExistsRequestWithSameSerialNumber() throws Exception {
        JwtAuthentication authentication = setupJwtAuthentication(userId, List.of("RECEIVER"));
        setupJwtMocks(authentication);

        RepairRequestDto request = RepairRequestDto.builder()
                .deviceId(deviceId)
                .typeOfRepair(TypeOfRepair.WARRANTY)
                .serialNumber(serialNumber)
                .saleDate(saleDate = LocalDate.now().minusDays(30))
                .defect(defect)
                .appearance(appearance)
                .customerFullName(customerFullName)
                .customerPatronymic(customerPatronymic)
                .customerAddress(customerAddress)
                .customerPhone(customerPhone)
                .acceptedById(1L)
                .build();

        serviceAscService.createRepairRequest(request);

        ResultActions resultActions = mockMvc.perform(
                        post("/asc/repair-request/create-repair-request")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", accessToken)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk());

        checkResponse(resultActions);

        verify(serviceAscService, times(2)).createRepairRequest(any(RepairRequestDto.class));
        verify(repairRequestRepository, times(2)).findBySerialNumber(any(String.class));
        verify(repairRequestRepository, times(1)).save(any(RepairRequest.class));
    }

    @Test
    void createRepairRequest_unauthorized() throws Exception {
        when(jwtUtils.validateToken(any(String.class))).thenReturn(false);

        mockMvc.perform(post("/asc/repair-request/create-repair-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", accessToken)
                        .content(objectMapper.writeValueAsString(new RepairRequestDto())))
                .andExpect(status().isUnauthorized());

        verifyNoMoreInteractions(serviceAscService);
    }

    private void checkResponse(ResultActions actions) throws Exception {
        actions
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.device").value(device))
                .andExpect(jsonPath("$.typeOfRepair").value(TypeOfRepair.WARRANTY.toString()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.serialNumber").value(serialNumber))
                .andExpect(jsonPath("$.saleDate").value(saleDate.toString()))
                .andExpect(jsonPath("$.defect").value(defect))
                .andExpect(jsonPath("$.appearance").value(appearance))
                .andExpect(jsonPath("$.customerFullName").value(customerFullName))
                .andExpect(jsonPath("$.customerPatronymic").value(customerPatronymic))
                .andExpect(jsonPath("$.customerAddress").value(customerAddress))
                .andExpect(jsonPath("$.customerPhone").value(customerPhone))
                .andExpect(jsonPath("$.acceptedBy").value(employeeFullName))
                .andExpect(jsonPath("$.nameOfServiceCenter").value(nameOfCenter))
                .andExpect(jsonPath("$.phoneNumber").value(phoneNumber))
                .andExpect(jsonPath("$.address").value(address));
    }
}
