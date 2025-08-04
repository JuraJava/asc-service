//package com.yurdan.ascService.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.client.RestTemplate;
//
///**
// * Этот класс — это Spring конфигурация, которая создаёт и предоставляет бин RestTemplate,
// * чтобы его можно было внедрять в другие классы
// */
//@Configuration
//public class RestTemplateConfig {
//    /**
//     * Метод создаёт новый экземпляр RestTemplate.
//     * Он будет доступен для внедрения (@Autowired, @RequiredArgsConstructor)
//     * в другие компоненты приложения.
//     */
//    @Bean
//    public RestTemplate restTemplate() {
//        return new RestTemplate();
//    }
//}

package com.yurdan.ascService.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Конфигурация RestTemplate через RestTemplateBuilder
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(10))     // таймаут на подключение
                .setReadTimeout(Duration.ofSeconds(20))       // таймаут на чтение ответа
                // .defaultHeader("Authorization", "Bearer ...") // можно задать дефолтные заголовки, если нужно
                .build();
    }
}