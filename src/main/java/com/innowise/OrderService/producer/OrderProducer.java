package com.innowise.OrderService.producer;

import com.innowise.common.event.OrderCreatedEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OrderProducer {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendOrderCreated(OrderCreatedEvent event) {
        String key = event.getUserId().toString();
        kafkaTemplate.send("create-order-topic", key, event);
    }
}
