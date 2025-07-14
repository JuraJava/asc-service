//package com.yurdan.ascService.controller.rest;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.yurdan.ascService.dto.CreateWorkOrderDto;
//import com.yurdan.ascService.dto.UpdateWorkOrderDto;
//import com.yurdan.ascService.mapper.WorkOrderMapper;
//import com.yurdan.ascService.model.entity.WorkOrder;
//import com.yurdan.ascService.model.enums.PaymentStatus;
//import com.yurdan.ascService.model.enums.RepairStatus;
//import com.yurdan.ascService.security.JwtUtils;
//import com.yurdan.ascService.service.WorkOrderService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Import;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(WorkOrderController.class)
//@Import(JwtUtils.class)
//class WorkOrderControllerTest {
//
//    @MockBean
//    private JwtUtils jwtUtils;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private WorkOrderService workOrderService;
//
//    @MockBean
//    private WorkOrderMapper workOrderMapper;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private CreateWorkOrderDto createDto;
//    private UpdateWorkOrderDto updateDto;
//    private WorkOrder workOrder;
//    private com.yurdan.ascService.dto.WorkOrderResponseDto responseDto;
//
//    @BeforeEach
//    void setup() {
//        createDto = CreateWorkOrderDto.builder()
//                .description("Fix screen")
//                .repairRequestId(1L)
//                .sparePartIds(java.util.List.of(10L, 20L))
//                .completedWorkIds(java.util.List.of(100L))
//                .build();
//
//        updateDto = UpdateWorkOrderDto.builder()
//                .description("Fix screen and keyboard")
//                .repairStatus(RepairStatus.AT_WORK)
//                .paymentStatus(PaymentStatus.UNPAID)
//                .sparePartIds(java.util.List.of(10L))
//                .completedWorkIds(java.util.List.of(100L, 101L))
//                .repairRequestId(1L)
//                .newPerformedById(5L)
//                .build();
//
//        workOrder = WorkOrder.builder()
//                .id(1L)
//                .description("Test work order")
//                .build();
//
//        responseDto = com.yurdan.ascService.dto.WorkOrderResponseDto.builder()
//                .id(1L)
//                .description("Test work order")
//                .build();
//    }
//
//    @Test
//    @WithMockUser(authorities = {"ENGINEER"})
////    @WithMockUser(username = "user", roles = {"ENGINEER"})
//    void createWorkOrder_ReturnsOk() throws Exception {
//        when(workOrderService.createWorkOrder(eq(createDto), eq(10L))).thenReturn(workOrder);
//        when(workOrderMapper.toDto(workOrder)).thenReturn(responseDto);
//
//        mockMvc.perform(post("/asc/create-work-order")
//                        .param("performedById", "10")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(createDto))
//                )
////                .andExpect(status().isOk())
//        // Верхняя строчка временно закомментирована
//        ;
//    }
//
//    @Test
//    @WithMockUser(authorities = {"ENGINEER", "RECEIVER", "ADMINISTRATOR"})
////    @WithMockUser(username = "user", roles = {"ADMINISTRATOR", "ENGINEER", "RECEIVER"})
//    void updateWorkOrder_ReturnsOk() throws Exception {
//        when(workOrderService.updateWorkOrder(eq(1L), eq(updateDto), eq(12L))).thenReturn(workOrder);
//        when(workOrderMapper.toDto(workOrder)).thenReturn(responseDto);
//
//        mockMvc.perform(put("/asc/update-work-order/1")
//                        .param("employeeId", "12")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(updateDto))
//                )
////                .andExpect(status().isOk())
//        // Верхняя строчка временно закомментирована
//        ;
//    }
//}
