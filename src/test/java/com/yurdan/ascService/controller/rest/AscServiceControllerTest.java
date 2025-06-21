package com.yurdan.ascService.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.service.ServiceAscService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AscServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceAscService serviceAscService;

    @Autowired
    private ObjectMapper objectMapper;

    private RepairResponseDto responseDto;

    @BeforeEach
    void setup() {
        responseDto = RepairResponseDto.builder()
                .id(1L)
                .createdAt(LocalDateTime.now())
                .typeOfRepair(TypeOfRepair.WARRANTY)
                .deviceId(2L)
                .serialNumber("R1212N24DLK")
                .saleDate(LocalDate.of(2025, 1, 1))
                .defect("Does not start")
                .appearance("Good")
                .cost(BigDecimal.valueOf(100))
                .customerFullName("Ivanov Ivan")
                .customerPatronymic("Petrovich")
                .customerAddress("Moscow, Russia")
                .customerPhone("89099111223")
                .acceptedById(2L)
                .name("Operator")
                .phoneNumber("89991122334")
                .address("Moscow")
                .build();
    }

    @Test
    void testCreateRepairRequest() throws Exception {
        RepairRequestDto requestDto = RepairRequestDto.builder()
                .typeOfRepair(TypeOfRepair.WARRANTY)
                .deviceId(1L)
                .serialNumber("R1242N24DLK")
                .defect("Не включается")
                .appearance("Целый, без повреждений")
                .customerFullName("Ivanov Ivan")
                .customerAddress("Москва, ул. Тверская")
                .customerPhone("89002397500")
                .acceptedById(2L)
                .build();

        Mockito.when(serviceAscService.createRepairRequest(any())).thenReturn(responseDto);

        mockMvc.perform(post("/asc/create-repair-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.customerFullName").value(responseDto.getCustomerFullName()));
    }

    @Test
    void testCreateRepairRequest_InvalidRequest() throws Exception {
        RepairRequestDto invalidDto = new RepairRequestDto();
        // Не заполняем обязательные поля — нарушаем валидацию

        mockMvc.perform(post("/asc/create-repair-request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetRepairRequests() throws Exception {
        Page<RepairResponseDto> page = new PageImpl<>(List.of(responseDto), PageRequest.of(0, 10), 1);

        Mockito.when(serviceAscService.getFilteredRepairRequests(
                any(), any(), any(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/asc/repair-requests")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(responseDto.getId()))
                .andExpect(jsonPath("$.content[0].customerFullName").value(responseDto.getCustomerFullName()));
    }
}