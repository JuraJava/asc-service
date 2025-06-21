package com.yurdan.ascService.controller.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.dto.CreateWorkOrderDto;
import com.yurdan.ascService.dto.UpdateWorkOrderDto;
import com.yurdan.ascService.dto.WorkOrderResponseDto;
import com.yurdan.ascService.model.entity.WorkOrder;
import com.yurdan.ascService.mapper.WorkOrderMapper;
import com.yurdan.ascService.service.WorkOrderService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkOrderController.class)
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WorkOrderService workOrderService;

    @MockBean
    private WorkOrderMapper workOrderMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateWorkOrder() throws Exception {
        CreateWorkOrderDto createDto = CreateWorkOrderDto.builder()
                .description("Test")
                .repairRequestId(1L)
                .completedWorkIds(List.of(1L))
                .sparePartIds(List.of(1L, 3L))
                .build();

        WorkOrder workOrder = WorkOrder.builder()
                .id(1L)
                .description("Test")
                .build();

        WorkOrderResponseDto responseDto = WorkOrderResponseDto.builder()
                .id(1L)
                .description("Test")
                .build();

        Mockito.when(workOrderService.createWorkOrder(any(), eq(3L))).thenReturn(workOrder);
        Mockito.when(workOrderMapper.toDto(workOrder)).thenReturn(responseDto);

        mockMvc.perform(post("/asc/create-work-order")
                        .param("performedById", "3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Test"));
    }

    @Test
    void testUpdateWorkOrder() throws Exception {
        UpdateWorkOrderDto updateDto = UpdateWorkOrderDto.builder()
                .description("Updated")
                .build();

        WorkOrder updatedWorkOrder = WorkOrder.builder()
                .id(1L)
                .description("Updated")
                .build();

        WorkOrderResponseDto responseDto = WorkOrderResponseDto.builder()
                .id(1L)
                .description("Updated")
                .build();

        Mockito.when(workOrderService.updateWorkOrder(eq(1L), any(), eq(200L), eq("RECEIVER")))
                .thenReturn(updatedWorkOrder);
        Mockito.when(workOrderMapper.toDto(updatedWorkOrder)).thenReturn(responseDto);

        mockMvc.perform(put("/asc/update-work-order/1")
                        .param("employeeId", "200")
                        .header("X-User-Role", "RECEIVER")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Updated"));
    }

    @Test
    void testGetWorkOrders() throws Exception {
        WorkOrderResponseDto dto = WorkOrderResponseDto.builder()
                .id(1L)
                .description("Test")
                .build();

        Page<WorkOrderResponseDto> page = new PageImpl<>(List.of(dto));

        Mockito.when(workOrderService.getFilteredWorkOrders(
                        any(), any(), any(), any(), any(), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/asc/work-orders")
                        .param("completedDate", LocalDate.now().toString())
                        .param("repairStatus", "AT_WORK")
                        .param("paymentStatus", "UNPAID")
                        .param("repairRequestId", "5")
                        .param("performedById", "10")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].description").value("Test"));
    }
}
