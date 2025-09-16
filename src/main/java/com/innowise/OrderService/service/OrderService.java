package com.innowise.OrderService.service;

import com.innowise.OrderService.dto.order.OrderUpdateRequestDto;
import com.innowise.OrderService.dto.orderItem.OrderItemRequestDto;
import com.innowise.OrderService.dto.userData.UserData;
import com.innowise.OrderService.dto.order.OrderRequestDto;
import com.innowise.OrderService.dto.order.OrderResponseDto;
import com.innowise.OrderService.entity.Item;
import com.innowise.OrderService.entity.Order;
import com.innowise.OrderService.entity.OrderItem;
import com.innowise.OrderService.mapper.OrderMapper;
import com.innowise.OrderService.producer.OrderProducer;
import com.innowise.OrderService.repository.ItemRepository;
import com.innowise.OrderService.repository.OrderRepository;
import com.innowise.common.event.OrderCreatedEvent;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import com.innowise.common.exception.TokenValidationCustomException;
import lombok.AllArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
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

        BigDecimal amount = calcAmount(savedOrder);

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(savedOrder.getId(), savedOrder.getUserId(), amount, savedOrder.getCreationDate());
        orderProducer.sendOrderCreated(orderCreatedEvent);

        return orderMapper.toDto(savedOrder);
    }


    @Transactional
    public OrderResponseDto getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundCustomException("User not found"));

        OrderResponseDto responseDto = orderMapper.toDto(order);
        responseDto.setUserInfo(getUserDetails(order.getUserId()));

        return responseDto;
    }


    @Transactional
    public List<OrderResponseDto> getOrdersByEmail(String email) {

        List<Order> orders = orderRepository.findByUserId(getUserDetailsByEmail(email).getId());
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
                new ResourceNotFoundCustomException("Item not found with id: " + id));

        order.setStatus(dto.getStatus());
//        order.setUserId(dto.getUserId());
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

    UserData getUserDetails(Long userId) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("No request context available");
        }

        String authorizationHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new TokenValidationCustomException("No valid JWT token found in request");
        }

        String accessToken = authorizationHeader.substring(7);
        UserData user = webClient.get()
                .uri("/api/v1/users/{id}", userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(UserData.class)
                .block();

        return user;
    }

    UserData getUserDetailsByEmail(String email) {

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("No request context available");
        }

        String authorizationHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new TokenValidationCustomException("No valid JWT token found in request");
        }

        String accessToken = authorizationHeader.substring(7);
        UserData user = webClient.get()
                .uri("/api/v1/users/email/{email}", email)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
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
