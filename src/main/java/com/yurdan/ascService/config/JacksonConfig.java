package com.yurdan.ascService.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Этот класс JacksonConfig — это конфигурация для настройки JSON-сериализации и десериализации в Spring Boot приложении
 * с использованием Jackson (библиотеки для работы с JSON).
 */
@Configuration
public class JacksonConfig {

    /**
     * Метод создает и возвращает настроенный экземпляр ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Поддержка Java 8 типов, включая LocalDateTime
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Настройка сериализации null - ов, опционально
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return mapper;
    }
}
