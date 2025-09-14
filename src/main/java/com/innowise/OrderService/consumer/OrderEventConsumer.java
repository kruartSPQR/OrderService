package com.innowise.OrderService.consumer;

import com.innowise.OrderService.dto.order.OrderRequestDto;
import com.innowise.OrderService.dto.order.OrderUpdateRequestDto;
import com.innowise.OrderService.entity.Order;
import com.innowise.OrderService.repository.OrderRepository;
import com.innowise.OrderService.service.OrderService;
import com.innowise.common.event.PaymentCreatedEvent;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class OrderEventConsumer {
    private final OrderService orderService;
    private OrderRepository orderRepository;

    @KafkaListener(topics = "payment-created-topic", groupId = "order-group")
    @Transactional
    public void handlePaymentCreated(PaymentCreatedEvent event, Acknowledgment ack){

        OrderUpdateRequestDto dto = new OrderUpdateRequestDto();

        Order order =  orderRepository.findById(event.getOrderId())
                .orElseThrow(() ->new ResourceNotFoundCustomException("Order not found with id: " + event.getOrderId()));

        if(!event.getAmount().equals(orderService.calcAmount(order))
        || !order.getStatus().equals("PENDING")){
            return;
        }

        if(event.getStatus().equals("SUCCESS")){
            dto.setStatus("PAID");
        }
        else{
            dto.setStatus("FAILED");
        }
        orderService.updateOrder(event.getOrderId(), dto);
        ack.acknowledge();
    }
}
