package com.cedacri.emailnotification.handler;

import com.cedacri.core.ProductCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ProductCreatedEventHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = "product-created-events-topic")
    public void handle(ProductCreatedEvent productCreatedEvent) {
        LOGGER.info("Received event : {}", productCreatedEvent.getTitle());
    }

}
