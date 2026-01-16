package org.blackcoffeecoding.audit.listeners;

import com.rabbitmq.client.Channel;
import org.blackcoffeecoding.device.events.DeviceCreatedEvent;
import org.blackcoffeecoding.device.events.DeviceDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RabbitListener(
        bindings = @QueueBinding(
                value = @Queue(
                        name = "device-audit-queue",
                        durable = "true",
                        arguments = {
                                @Argument(name = "x-dead-letter-exchange", value = "dlx-exchange"),
                                @Argument(name = "x-dead-letter-routing-key", value = "dlq.audit")
                        }),
                exchange = @Exchange(name = "devices-exchange", type = "topic", durable = "true"),
                key = {"device.created", "device.deleted"}
        )
)
public class DeviceEventListener {

    private static final Logger log = LoggerFactory.getLogger(DeviceEventListener.class);
    private final Set<Long> processedEvents = ConcurrentHashMap.newKeySet();

    @RabbitHandler
    public void handleDeviceCreated(@Payload DeviceCreatedEvent event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            if (!processedEvents.add(event.id())) {
                log.warn("Дубликат события создания ID: {}. Пропускаем.", event.id());
                channel.basicAck(deliveryTag, false);
                return;
            }
            log.info("AUDIT: Получено событие создания: {}", event);
            if ("CRASH".equalsIgnoreCase(event.name())) throw new RuntimeException("Boom!");
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Ошибка обработки создания: {}. Отправляем в DLQ.", e);
            channel.basicNack(deliveryTag, false, false);
        }
    }

    @RabbitHandler
    public void handleDeviceDeleted(@Payload DeviceDeletedEvent event,
                                    Channel channel,
                                    @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        try {
            log.warn("AUDIT: Устройство удалено с ID: {}", event.id());
            processedEvents.remove(event.id());
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Ошибка при удалении: {}", event, e);
            channel.basicNack(deliveryTag, false, false);
        }
    }
}