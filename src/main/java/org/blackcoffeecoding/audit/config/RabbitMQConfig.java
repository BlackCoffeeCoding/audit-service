package org.blackcoffeecoding.audit.config;

import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class RabbitMQConfig {
    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        // Разрешаем пакеты с нашими ивентами [cite: 718-719]
        converter.setAllowedListPatterns(List.of("org.blackcoffeecoding.device.events.*"));
        return converter;
    }
}