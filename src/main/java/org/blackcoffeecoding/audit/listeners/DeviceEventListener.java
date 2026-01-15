package org.blackcoffeecoding.audit.listeners;

import org.blackcoffeecoding.device.events.DeviceCreatedEvent;
import org.blackcoffeecoding.device.events.DeviceDeletedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeviceEventListener {

    private static final Logger log = LoggerFactory.getLogger(DeviceEventListener.class);

    // Слушатель создания [cite: 714]
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "device-created-queue", durable = "true"),
            exchange = @Exchange(name = "devices-exchange", type = "topic"),
            key = "device.created"
    ))
    public void handleDeviceCreated(DeviceCreatedEvent event) {
        log.info("AUDIT: New device created: {}", event);
    }

    // Слушатель удаления (Самостоятельная работа) [cite: 728]
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "device-deleted-queue", durable = "true"),
            exchange = @Exchange(name = "devices-exchange", type = "topic"),
            key = "device.deleted"
    ))
    public void handleDeviceDeleted(DeviceDeletedEvent event) {
        log.warn("AUDIT: Device deleted with ID: {}", event.id());
    }
}