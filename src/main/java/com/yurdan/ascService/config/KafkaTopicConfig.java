package com.yurdan.ascService.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic payInTopic() {
        return new NewTopic("PAY.IN", 1, (short) 1);
    }

    @Bean
    public NewTopic payOutTopic() {
        return new NewTopic("PAY.OUT", 1, (short) 1);
    }
}