package com.yurdan.ascService.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Задача — логировать тело (body) HTTP-запроса на уровне фильтра.
 * Логирует тело запроса в логах
 * Этот класс тесно связан с предыдущим CachedBodyHttpServletRequest,
 * который позволяет прочитать тело запроса многократно
 */
@Slf4j
@Component
public class RequestBodyLoggingFilter implements Filter {

    /**
     * Этот метод вызывается при каждом HTTP-запросе, поступающем в приложение
     * Проверяется, что пришёл HTTP-запрос.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest httpServletRequest) {
            CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpServletRequest);

            String body = new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            log.info("Body: {}", body);  //  Вот эта строка и будет логировать тело запроса

            /**
             * Передаёт обёрнутый запрос дальше по цепочке (в контроллер, сервисы и т.д.).
             * Если бы был  передал оригинальный httpServletRequest,
             * контроллер уже не смог бы прочитать тело (оно было бы "поглощено").
             */
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
