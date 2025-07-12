package com.yurdan.ascService.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.dto.RepairRequestDto;
import com.yurdan.ascService.dto.RepairResponseDto;
import com.yurdan.ascService.dto.UpdateDefectDto;
import com.yurdan.ascService.model.enums.TypeOfRepair;
import com.yurdan.ascService.security.JwtUtils;
import com.yurdan.ascService.service.ServiceAscService;
import com.yurdan.ascService.mapper.RepairRequestMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RepairRequestController.class)
class RepairRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceAscService serviceAscService;

    @MockBean
    private RepairRequestMapper repairRequestMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtils jwtUtils;

    @Test
    @WithMockUser(authorities = {"RECEIVER"})
    void testCreateRepairRequest() throws Exception {
        RepairRequestDto requestDto = RepairRequestDto.builder()
                .deviceId(1L)
                .typeOfRepair(TypeOfRepair.WARRANTY)
                .serialNumber("SN123456")
                .defect("Defect example")
                .appearance("Good")
                .customerFullName("Ivan Ivanov")
                .customerAddress("Some address")
                .customerPhone("+70000000000")
                .acceptedById(10L)
                .build();

        RepairResponseDto responseDto = new RepairResponseDto(
                1L,
                "DeviceName Black",
                TypeOfRepair.WARRANTY,
                LocalDateTime.now(),
                "SN123456",
                LocalDate.now(),
                "Defect example",
                "Good",
                "Ivan Ivanov",
                null,
                "Some address",
                "+70000000000",
                "Employee Name",
                "Service Center",
                "+71111111111",
                "Center Address"
        );

        // Мокаем сервис и маппер
        when(serviceAscService.createRepairRequest(any(RepairRequestDto.class))).thenReturn(null); // сущность не нужна для теста контроллера
        when(repairRequestMapper.toDto(any())).thenReturn(responseDto);

        mockMvc.perform(post("/asc/repair-request/create-repair-request")
                        .with(csrf())  // обязательно для POST, PATCH и DELETE с security
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.device").value("DeviceName Black"))
                .andExpect(jsonPath("$.typeOfRepair").value("WARRANTY"))
                .andExpect(jsonPath("$.serialNumber").value("SN123456"))
                .andExpect(jsonPath("$.defect").value("Defect example"))
                .andExpect(jsonPath("$.customerFullName").value("Ivan Ivanov"));

        verify(serviceAscService).createRepairRequest(any(RepairRequestDto.class));
        verify(repairRequestMapper).toDto(any());
    }

    @Test
    @WithMockUser // любая авторизация, так как метод без @PreAuthorize
    void testGetRepairRequests() throws Exception {
        RepairResponseDto dto = new RepairResponseDto(
                1L,
                "DeviceName Black",
                TypeOfRepair.WARRANTY,
                LocalDateTime.now(),
                "SN123456",
                LocalDate.now(),
                "Defect example",
                "Good",
                "Ivan Ivanov",
                null,
                "Some address",
                "+70000000000",
                "Employee Name",
                "Service Center",
                "+71111111111",
                "Center Address"
        );

        Page<RepairResponseDto> page = new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        when(serviceAscService.getFilteredRepairRequests(
                any(), any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        mockMvc.perform(get("/asc/repair-request/repair-requests")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,desc"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].device").value("DeviceName Black"))
                .andExpect(jsonPath("$.content[0].typeOfRepair").value("WARRANTY"));

        verify(serviceAscService).getFilteredRepairRequests(
                any(), any(), any(), any(), any(), any(), any(Pageable.class)
        );
    }

    @Test
    @WithMockUser(authorities = {"RECEIVER"})
    void testUpdateDefect() throws Exception {
        UpdateDefectDto updateDto = new UpdateDefectDto();
        updateDto.setId(1L);
        updateDto.setDefect("Updated defect");

        RepairResponseDto responseDto = new RepairResponseDto(
                1L,
                "DeviceName Black",
                TypeOfRepair.WARRANTY,
                LocalDateTime.now(),
                "SN123456",
                LocalDate.now(),
                "Updated defect",
                "Good",
                "Ivan Ivanov",
                null,
                "Some address",
                "+70000000000",
                "Employee Name",
                "Service Center",
                "+71111111111",
                "Center Address"
        );

        when(serviceAscService.updateDefect(any(UpdateDefectDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/asc/repair-request/repair-request/update-defect")
                        .with(csrf())  // важно для PATCH запросов
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.defect").value("Updated defect"));

        verify(serviceAscService).updateDefect(any(UpdateDefectDto.class));
    }
}