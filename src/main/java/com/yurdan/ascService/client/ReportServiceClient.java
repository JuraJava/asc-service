package com.yurdan.ascService.client;

import com.yurdan.ascService.dto.HttpLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Этот класс отправляет логи (DTO-объекты HttpLogDto) в отдельный микросервис — report-service.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportServiceClient {

    private final RestTemplate restTemplate;

    //    private static final String REPORT_SERVICE_URL = "http://localhost:8083/report/logs";
    @Value("${report.service.url}")
    private String reportServiceUrl;

    /**
     * Метод принимает лог в виде объекта HttpLogDto и отправляет его в report-service
     */
    public void sendHttpLog(HttpLogDto logDto) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<HttpLogDto> request = new HttpEntity<>(logDto, headers);
//            restTemplate.postForEntity(REPORT_SERVICE_URL, request, Void.class);
            restTemplate.postForEntity(reportServiceUrl, request, Void.class);
        } catch (Exception e) {
            log.warn("Failed to send log to report-service: {}", e.getMessage());
        }
    }
}


