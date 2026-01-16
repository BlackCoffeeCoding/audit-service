package org.blackcoffeecoding.audit.listeners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

@Component
public class DlqListener {
    private static final Logger log = LoggerFactory.getLogger(DlqListener.class);

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "device-audit-queue.dlq", durable = "true"),
                    exchange = @Exchange(name = "dlx-exchange", type = "topic", durable = "true"),
                    key = "dlq.audit"
            )
    )
    public void handleDlqMessages(Object failedMessage) {
        log.error("!!! ALERT DLQ: {}", failedMessage);
    }
}