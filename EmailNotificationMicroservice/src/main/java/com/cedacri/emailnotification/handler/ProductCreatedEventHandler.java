package com.cedacri.emailnotification.handler;

import com.cedacri.core.ProductCreatedEvent;
import com.cedacri.emailnotification.entity.ProcessedEvent;
import com.cedacri.emailnotification.exception.NonRetryableException;
import com.cedacri.emailnotification.exception.RetryableException;
import com.cedacri.emailnotification.repository.ProcessedEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Component
@KafkaListener(topics = "product-created-events-topic")
public class ProductCreatedEventHandler {

    private final ProcessedEventRepository processedEventRepository;

    private final RestTemplate restTemplate;

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public ProductCreatedEventHandler(RestTemplate restTemplate, ProcessedEventRepository processedEventRepository) {
        this.processedEventRepository = processedEventRepository;
        this.restTemplate = restTemplate;
    }

    @Transactional
    @KafkaHandler
    public void handle(@Payload ProductCreatedEvent productCreatedEvent,
                       @Header("messageId") String messageId,
                       @Header(KafkaHeaders.RECEIVED_KEY) String messageKey) {
        LOGGER.info("Received event : {}, productId : {}", productCreatedEvent.getTitle(), productCreatedEvent.getId());

        String url = "http://localhost:8090/response/200";

        ProcessedEvent processedEvent = processedEventRepository.findByMessageId(messageId);

        if(processedEvent != null) {
            LOGGER.info("Duplicate messageId : {}", messageId);
            return;
        }

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

            if(response.getStatusCode().value() == HttpStatus.OK.value()) {
                LOGGER.info("Received response : {}", response.getBody());
            }
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage());
            throw new RetryableException(e);
        } catch (HttpServerErrorException e) {
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        } catch (Exception e){
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }

        try {
            ProcessedEvent savingProcessed = new ProcessedEvent(messageId, productCreatedEvent.getId());
            processedEventRepository.save(savingProcessed);
        } catch (DataIntegrityViolationException e){
            LOGGER.error(e.getMessage());
            throw new NonRetryableException(e);
        }
    }

}
