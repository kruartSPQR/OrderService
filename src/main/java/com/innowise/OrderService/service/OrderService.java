package com.innowise.OrderService.service;

import com.innowise.OrderService.dto.order.OrderUpdateRequestDto;
import com.innowise.OrderService.dto.orderItem.OrderItemRequestDto;
import com.innowise.OrderService.dto.userData.UserData;
import com.innowise.OrderService.dto.order.OrderRequestDto;
import com.innowise.OrderService.dto.order.OrderResponseDto;
import com.innowise.OrderService.entity.Item;
import com.innowise.OrderService.entity.Order;
import com.innowise.OrderService.entity.OrderItem;
import com.innowise.OrderService.mapper.ItemMapper;
import com.innowise.OrderService.mapper.OrderMapper;
import com.innowise.OrderService.producer.OrderProducer;
import com.innowise.OrderService.repository.ItemRepository;
import com.innowise.OrderService.repository.OrderRepository;
import com.innowise.common.event.OrderCreatedEvent;
import com.innowise.common.exception.DuplicateResourceCustomException;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor

public class OrderService {
    private OrderRepository orderRepository;
    private ItemRepository itemRepository;
    private OrderMapper orderMapper;
    private ItemMapper itemMapper;
    private WebClient webClient;
    private OrderProducer orderProducer;

    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto dto) {

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain items");
        }

        Order order = new Order();
        order.setUserId(dto.getUserId());
        order.setStatus("PENDING");
        order.setCreationDate(LocalDateTime.now());

        for (OrderItemRequestDto itemDto : dto.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId()).orElseThrow(() ->
                    new ResourceNotFoundCustomException("Item not found with id: " + itemDto.getItemId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDto.getQuantity());
            order.getOrderItems().add(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        return orderMapper.toDto(savedOrder);
    }
    @Transactional
public void sendOrderCreatedEvent(Long orderId) {

    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundCustomException("Order not found with ID: " + orderId));

    String userId = getOrderById(orderId).getUserId();

    if (!order.getUserId().equals(userId)) {
        throw new ResourceNotFoundCustomException("Order does not belong to user: " + userId);
    }

    if (order.getStatus().equals("PAID") || order.getStatus().equals("FAILED")) {
        throw new IllegalStateException("Order is already paid or failed");
    }

    BigDecimal amount = calcAmount(order);

       OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(order.getId(), order.getUserId(), amount, order.getCreationDate());
       orderProducer.sendOrderCreated(orderCreatedEvent);
}

    @Transactional
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException("Order not found"));

        OrderResponseDto responseDto = orderMapper.toDto(order);
        responseDto.setUserInfo(getUserDetails(order.getUserId()));

        return responseDto;
    }


    @Transactional
    public List<OrderResponseDto> getOrdersByEmail(String email) {

        List<Order> orders = orderRepository.findByUserId(email);
        if (orders == null) {
            throw new ResourceNotFoundCustomException("Orders not found");
        }
        List<OrderResponseDto> orderDtos = orders.stream()
                .map(order -> {
                    OrderResponseDto dto = orderMapper.toDto(order);
                    dto.setUserInfo(getUserDetails(order.getUserId()));
                    return dto;
                })
                .toList();

        return orderDtos;
    }

    @Transactional
    public List<OrderResponseDto> getOrdersByIds(List<Long> ids) {

        List<Order> orders = orderRepository.findAllById(ids);

        return orders.stream().map(order ->
                {
                    OrderResponseDto responseDto = orderMapper.toDto(order);
                    responseDto.setUserInfo(getUserDetails(order.getUserId()));
                    return responseDto;
                })
                .toList();
    }

    public List<OrderResponseDto> getOrdersByStatus(String status) {

        List<Order> orders = orderRepository.findAllByStatus(status);

        return orders.stream().map(order ->
                {
                    OrderResponseDto responseDto = orderMapper.toDto(order);
                    responseDto.setUserInfo(getUserDetails(order.getUserId()));
                    return responseDto;
                })
                .toList();
    }

    @Transactional
    public OrderResponseDto updateOrder(Long id, OrderUpdateRequestDto dto) {
        Order order = orderRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundCustomException("Order not found with id: " + id));

        if(!order.getStatus().equals("PENDING")) {
            throw new DuplicateResourceCustomException("Cannot update order that is not pending");
        }

        order.setStatus(dto.getStatus());

        order.getOrderItems().clear();

        for (OrderItemRequestDto itemDto : dto.getItems()) {
            Item item = itemRepository.findById(itemDto.getItemId()).orElseThrow(() ->
                    new ResourceNotFoundCustomException("Item not found with id: " + itemDto.getItemId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setItem(item);
            orderItem.setQuantity(itemDto.getQuantity());
            order.getOrderItems().add(orderItem);
        }

        Order updatedOrder = orderRepository.save(order);

        return orderMapper.toDto(updatedOrder);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findOrderById(id);
        if (!orderRepository.existsById(id)) {
            throw new ResourceNotFoundCustomException("Order not found with id: " + id);
        }
        orderRepository.delete(order);
    }

    UserData getUserDetails(String userId) {

        UserData user = webClient.get()
                .uri("/api/v1/users/email/{email}", userId)
                .retrieve()
                .bodyToMono(UserData.class)
                .block();

        return user;
    }

    public BigDecimal calcAmount(Order savedOrder) {
        return savedOrder.getOrderItems().stream()
                .map(oi -> BigDecimal.valueOf(oi.getQuantity() * oi.getItem().getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
