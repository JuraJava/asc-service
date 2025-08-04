package com.yurdan.ascService.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yurdan.ascService.client.ReportServiceClient;
import com.yurdan.ascService.dto.HttpLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * Это  перехватчик REST-ответов, который:
 * работает в asc-service;
 * автоматически перехватывает все REST-ответы из контроллеров;
 * логирует на уровне INFO:
 * URL запроса;
 * HTTP-статус ответа;
 * тело ответа, если оно логируемого типа (JSON, текст).
 */

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class ResponseBodyLoggerAdvice implements ResponseBodyAdvice<Object> {

    private final ReportServiceClient reportServiceClient;
    // преобразует объект Java (тело запроса в логах) в строку JSON
    private final ObjectMapper objectMapper;

    /**
     * Этот метод обрабатывает все ответы из контроллеров
     */
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    /**
     * Это основной метод, который вызывается перед отправкой тела ответа клиенту
     */
    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        try {
            /**
             * проверка типа запроса/ответа
             */
            if (!(request instanceof ServletServerHttpRequest servletRequest) ||
                    !(response instanceof ServletServerHttpResponse servletResponse)) {
                return body;
            }
    /**
     * извлекаем URL и HTTP-статус
     */
            String url = servletRequest.getServletRequest().getRequestURI();
            int status = servletResponse.getServletResponse().getStatus();
            /**
             * формируется тело ответа для логирования
             */
            String responseBody = MediaType.APPLICATION_JSON.includes(selectedContentType)
                    ? objectMapper.writeValueAsString(body)
                    : "<non-loggable content>";

            /**
             * лог в стандартный лог-файл или консоль
             */
            log.info("[RESPONSE] URL: {} | Status: {} | Body: {}", url, status, responseBody);

            /**
             * отправка в report-service
             */
            HttpLogDto logDto = HttpLogDto.builder()
                    .url(url)
                    .httpStatus(status)
                    .responseBody(responseBody)
                    .build();
            reportServiceClient.sendHttpLog(logDto);
        /**
         * обработка ошибок
         */
        } catch (Exception e) {
            log.warn("Failed to process response for logging: {}", e.getMessage());
        }

        return body;
    }
}
