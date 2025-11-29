package com.flashsale.ticketing.config;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.ProducerListener;

@Configuration
public class KafkaLoggingConfig {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Bean
    public ProducerListener<Object, Object> kafkaProducerListener() {
        return new ProducerListener<Object, Object>() {

            @Override
            public void onSuccess(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata) {
                log.info("📤 KAFKA SEND SUCCESS | Topic: {} | Key: {} | Payload: {}",
                        producerRecord.topic(), producerRecord.key(), producerRecord.value());
            }

            @Override
            public void onError(ProducerRecord<Object, Object> producerRecord, RecordMetadata recordMetadata, Exception exception) {
                log.error("❌ KAFKA SEND FAILED | Topic: {} | Error: {}",
                        producerRecord.topic(), exception.getMessage());
            }
        };
    }
}