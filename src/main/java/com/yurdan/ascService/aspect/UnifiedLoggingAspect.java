package com.yurdan.ascService.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class UnifiedLoggingAspect {

    final HttpServletRequest request;

    // Pointcut для GET и POST запросов
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void loggableEndpoints() {}

    // Pointcut для аннотации @LogHttpBody
    @Pointcut("@annotation(com.yurdan.ascService.logging.LogHttpBody)")
    public void logHttpBodyAnnotation() {}

    // Логирование входящего запроса
    @Before("loggableEndpoints()")
    public void logRequest(JoinPoint joinPoint) {
        String url = request.getRequestURI();
        String method = request.getMethod();
        Map<String, String> headers = getHeadersInfo(request);

        StringBuilder logMessage = new StringBuilder();
        logMessage.append("INCOMING REQUEST:\n");
        logMessage.append("URL: ").append(url).append("\n");
        logMessage.append("Method: ").append(method).append("\n");
        logMessage.append("Headers: ").append(headers);

        log.info(logMessage.toString());
    }

    // Логирование ответа
    @AfterReturning(pointcut = "logHttpBodyAnnotation()", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        String url = request.getRequestURI();

        if (result instanceof ResponseEntity<?> response) {
            log.info("RESPONSE for URL [{}]: {}", url, response.getBody());
        } else {
            log.info("RESPONSE for URL [{}]: {}", url, result);
        }
    }

    // Логирование исключения
    @AfterThrowing(pointcut = "logHttpBodyAnnotation()", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        String url = request.getRequestURI();

        log.error("EXCEPTION for URL [{}] - Status: 500, Body: {}", url, ex.getMessage());
    }

    // Получение заголовков запроса
    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                String value = request.getHeader(key);
                map.put(key, value);
            }
        }
        return map;
    }
}
