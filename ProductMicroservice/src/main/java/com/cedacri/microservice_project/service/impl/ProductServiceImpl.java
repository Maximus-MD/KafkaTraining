package com.cedacri.microservice_project.service.impl;

import com.cedacri.core.ProductCreatedEvent;
import com.cedacri.microservice_project.dto.ProductDto;
import com.cedacri.microservice_project.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public String createProduct(ProductDto productDto) throws ExecutionException, InterruptedException {
        String productId = UUID.randomUUID().toString();

        String topic = "product-created-events-topic";

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(productId, productDto.getTitle(), productDto.getPrice(), productDto.getQuantity());

        ProducerRecord<String, ProductCreatedEvent> record = new ProducerRecord<>(
                topic,
                productId,
                productCreatedEvent
        );

        record.headers().add("messageId", UUID.randomUUID().toString().getBytes());

        SendResult<String, ProductCreatedEvent> result =
                kafkaTemplate.send(record).get();

        LOGGER.info("Topic : {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition : {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset : {}", result.getRecordMetadata().offset());

        LOGGER.info("Returned value : {}", productId);

        return productId;
    }
}
