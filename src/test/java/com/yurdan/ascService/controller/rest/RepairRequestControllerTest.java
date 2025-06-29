//package com.yurdan.ascService.controller.rest;

//import com.yurdan.ascService.dto.*;
//import com.yurdan.ascService.mapper.RepairRequestMapper;
//import com.yurdan.ascService.model.entity.RepairRequest;
//import com.yurdan.ascService.model.enums.RequestStatus;
//import com.yurdan.ascService.model.enums.TypeOfRepair;
//import com.yurdan.ascService.service.ServiceAscService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class AscServiceControllerTest {
//
//    private ServiceAscService serviceAscService;
//    private RepairRequestMapper repairRequestMapper;
//    private AscServiceController controller;
//
//    @BeforeEach
//    void setUp() {
//        serviceAscService = mock(ServiceAscService.class);
//        repairRequestMapper = mock(RepairRequestMapper.class);
//        controller = new AscServiceController(serviceAscService, repairRequestMapper);
//    }
//
//    @Test
//    void testCreateRepairRequest() {
//        // Given
//        RepairRequestDto requestDto = RepairRequestDto.builder()
//                .deviceId(1L)
//                .typeOfRepair(TypeOfRepair.WARRANTY)
//                .serialNumber("ABC123")
//                .saleDate(LocalDate.now())
//                .defect("Does not turn on")
//                .appearance("Scratched")
//                .customerFullName("John Doe")
//                .customerPatronymic("Ivanovich")
//                .customerAddress("123 Main St")
//                .customerPhone("+123456789")
//                .acceptedById(2L)
//                .build();
//
//        RepairRequest entity = new RepairRequest();
//        entity.setId(1L);
//
//        RepairResponseDto responseDto = RepairResponseDto.builder()
//                .id(1L)
//                .serialNumber("ABC123")
//                .defect("Does not turn on")
//                .customerFullName("John Doe")
//                .build();
//
//        when(serviceAscService.createRepairRequest(requestDto)).thenReturn(entity);
//        when(repairRequestMapper.toDto(entity)).thenReturn(responseDto);
//
//        // When
//        ResponseEntity<RepairResponseDto> response = controller.createRepairRequest(requestDto);
//
//        // Then
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(responseDto, response.getBody());
//
//        verify(serviceAscService).createRepairRequest(requestDto);
//        verify(repairRequestMapper).toDto(entity);
//    }
//
//    @Test
//    void testGetRepairRequests() {
//        // Given
//        RepairResponseDto dto = RepairResponseDto.builder()
//                .id(1L)
//                .serialNumber("SN001")
//                .customerFullName("Alice Smith")
//                .defect("No sound")
//                .build();
//
//        Page<RepairResponseDto> page = new PageImpl<>(List.of(dto));
//        PageRequest pageRequest = PageRequest.of(0, 10);
//
//        when(serviceAscService.getFilteredRepairRequests(
//                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageRequest)
//        )).thenReturn(page);
//
//        // When
//        ResponseEntity<Page<RepairResponseDto>> response = controller.getRepairRequests(
//                null, null, null, null, null, null, null, null
//        );
//
//        // Then
//        assertEquals(200, response.getStatusCodeValue());
//        assertNotNull(response.getBody());
//        assertEquals(1, response.getBody().getTotalElements());
//        assertEquals("Alice Smith", response.getBody().getContent().get(0).getCustomerFullName());
//
//        verify(serviceAscService).getFilteredRepairRequests(
//                isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(pageRequest)
//        );
//    }
//
//    @Test
//    void testGetRepairRequestsWithParams() {
//        // Given
//        LocalDate date = LocalDate.of(2024, 1, 1);
//        TypeOfRepair repairType = TypeOfRepair.NON_WARRANTY;
//        RequestStatus status = RequestStatus.ACCEPTED;
//        Long deviceId = 10L;
//        String customerName = "Bob Johnson";
//        Long acceptedById = 5L;
//        int page = 1;
//        int size = 5;
//
//        PageRequest pageRequest = PageRequest.of(page, size);
//        RepairResponseDto dto = new RepairResponseDto();
//        Page<RepairResponseDto> resultPage = new PageImpl<>(List.of(dto));
//
//        when(serviceAscService.getFilteredRepairRequests(
//                eq(date), eq(repairType), eq(status), eq(deviceId),
//                eq(customerName), eq(acceptedById), eq(pageRequest)
//        )).thenReturn(resultPage);
//
//        // When
//        ResponseEntity<Page<RepairResponseDto>> response = controller.getRepairRequests(
//                date, repairType, status, deviceId, customerName, acceptedById, page, size
//        );
//
//        // Then
//        assertEquals(200, response.getStatusCodeValue());
//        verify(serviceAscService).getFilteredRepairRequests(
//                eq(date), eq(repairType), eq(status), eq(deviceId),
//                eq(customerName), eq(acceptedById), eq(pageRequest)
//        );
//    }
//
//    @Test
//    void testUpdateDefect() {
//        // Given
//        UpdateDefectDto updateDto = new UpdateDefectDto();
//        updateDto.setId(1L);
//        updateDto.setDefect("Updated defect");
//
//        RepairResponseDto responseDto = RepairResponseDto.builder()
//                .id(1L)
//                .defect("Updated defect")
//                .build();
//
//        when(serviceAscService.updateDefect(updateDto)).thenReturn(responseDto);
//
//        // When
//        ResponseEntity<RepairResponseDto> response = controller.updateDefect(updateDto);
//
//        // Then
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("Updated defect", response.getBody().getDefect());
//
//        verify(serviceAscService).updateDefect(updateDto);
//    }
//}

// А если с использованием Spring MockMvc, то ниже:

//package com.yurdan.ascService.controller.rest;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yurdan.ascService.dto.*;
//import com.yurdan.ascService.mapper.RepairRequestMapper;
//import com.yurdan.ascService.model.entity.RepairRequest;
//import com.yurdan.ascService.model.enums.TypeOfRepair;
//import com.yurdan.ascService.service.ServiceAscService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(AscServiceController.class)
//public class AscServiceControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ServiceAscService serviceAscService;
//
//    @MockBean
//    private RepairRequestMapper repairRequestMapper;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private RepairRequestDto validRequestDto;
//    private RepairRequest repairRequestEntity;
//    private RepairResponseDto responseDto;
//
//    @BeforeEach
//    void setUp() {
//        validRequestDto = RepairRequestDto.builder()
//                .deviceId(1L)
//                .typeOfRepair(TypeOfRepair.WARRANTY)
//                .serialNumber("SN12345")
//                .saleDate(LocalDate.now())
//                .defect("Doesn't work")
//                .appearance("Good")
//                .customerFullName("John Doe")
//                .customerPatronymic("Ivanovich")
//                .customerAddress("123 Main St")
//                .customerPhone("+123456789")
//                .acceptedById(2L)
//                .build();
//
//        repairRequestEntity = new RepairRequest();
//        repairRequestEntity.setId(1L);
//
//        responseDto = RepairResponseDto.builder()
//                .id(1L)
//                .serialNumber("SN12345")
//                .defect("Doesn't work")
//                .customerFullName("John Doe")
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    void createRepairRequest_success() throws Exception {
//        Mockito.when(serviceAscService.createRepairRequest(any(RepairRequestDto.class)))
//                .thenReturn(repairRequestEntity);
//        Mockito.when(repairRequestMapper.toDto(any(RepairRequest.class)))
//                .thenReturn(responseDto);
//
//        mockMvc.perform(post("/asc/create-repair-request")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(validRequestDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(responseDto.getId()))
//                .andExpect(jsonPath("$.serialNumber").value(responseDto.getSerialNumber()));
//    }
//
//    @Test
//    void createRepairRequest_validationFail() throws Exception {
//        RepairRequestDto invalidDto = new RepairRequestDto(); // пустой, не проходит @NotNull и @NotBlank
//
//        mockMvc.perform(post("/asc/create-repair-request")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(invalidDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.errors").doesNotExist()); // может быть адаптировано под вашу ошибку
//    }
//
//    @Test
//    void getRepairRequests_success() throws Exception {
//        PageImpl<RepairResponseDto> page = new PageImpl<>(List.of(responseDto));
//
//        Mockito.when(serviceAscService.getFilteredRepairRequests(
//                        any(), any(), any(), any(), any(), any(), any(Pageable.class)))
//                .thenReturn(page);
//
//        mockMvc.perform(get("/asc/repair-requests")
//                        .param("page", "0")
//                        .param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.content[0].serialNumber").value(responseDto.getSerialNumber()));
//    }
//
//    @Test
//    void updateDefect_success() throws Exception {
//        UpdateDefectDto updateDto = new UpdateDefectDto();
//        updateDto.setId(1L);
//        updateDto.setDefect("New defect");
//
//        RepairResponseDto updatedResponse = RepairResponseDto.builder()
//                .id(1L)
//                .defect("New defect")
//                .build();
//
//        Mockito.when(serviceAscService.updateDefect(any(UpdateDefectDto.class)))
//                .thenReturn(updatedResponse);
//
//        mockMvc.perform(patch("/asc/repair-request/update-defect")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.defect").value("New defect"));
//    }
//}