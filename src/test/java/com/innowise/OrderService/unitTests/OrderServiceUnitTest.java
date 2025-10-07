package com.innowise.OrderService.unitTests;

import com.innowise.OrderService.dto.order.OrderRequestDto;
import com.innowise.OrderService.dto.order.OrderResponseDto;
import com.innowise.OrderService.dto.order.OrderUpdateRequestDto;
import com.innowise.OrderService.dto.orderItem.OrderItemRequestDto;
import com.innowise.OrderService.dto.userData.UserData;
import com.innowise.OrderService.entity.Item;
import com.innowise.OrderService.entity.Order;
import com.innowise.OrderService.entity.OrderItem;
import com.innowise.OrderService.mapper.ItemMapper;
import com.innowise.OrderService.mapper.OrderMapper;
import com.innowise.OrderService.producer.OrderProducer;
import com.innowise.OrderService.repository.ItemRepository;
import com.innowise.OrderService.repository.OrderRepository;
import com.innowise.OrderService.service.OrderService;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceUnitTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private WebClient webClient;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderService orderService;

    private OrderRequestDto orderRequestDto;
    private OrderUpdateRequestDto orderUpdatedRequestDto;
    private Order orderEntity;
    private OrderResponseDto orderResponseDto;
    private UserData userData;
    private OrderItem orderItem;
    private Item item;

    @BeforeEach
    void setUp() {
        orderRequestDto = new OrderRequestDto();
        orderRequestDto.setUserId("test@example.com");
        orderRequestDto.setItems(List.of(new OrderItemRequestDto(1L, 2)));

        orderUpdatedRequestDto = new OrderUpdateRequestDto();
        orderUpdatedRequestDto.setUserId("test@example.com");
        orderUpdatedRequestDto.setItems(List.of(new OrderItemRequestDto(1L, 2)));

        orderEntity = new Order();
        orderEntity.setId(1L);
        orderEntity.setUserId("test@example.com");
        orderEntity.setStatus("PENDING");
        orderEntity.setCreationDate(LocalDateTime.now());

        item = new Item();
        item.setId(1L);
        item.setPrice(50.0);

        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setQuantity(2);
        orderItem.setItem(item);
        orderItem.setOrder(orderEntity);

        orderEntity.setOrderItems(new ArrayList<>(List.of(orderItem)));

        orderResponseDto = new OrderResponseDto();
        orderResponseDto.setId(1L);
        orderResponseDto.setUserId("test@example.com");
        orderResponseDto.setStatus("PENDING");
        orderResponseDto.setCreationDate(orderEntity.getCreationDate());
        orderResponseDto.setUserInfo(null);

        userData = new UserData();
        userData.setId(1L);
        userData.setEmail("test@example.com");
    }

    @Test
    void testCreateOrder() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(any(Order.class))).thenReturn(orderEntity);
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.createOrder(orderRequestDto);

        assertNotNull(result);
        assertEquals(orderResponseDto.getUserId(), result.getUserId());
        assertEquals(orderResponseDto.getStatus(), result.getStatus());
        assertEquals(1, orderEntity.getOrderItems().size());

        verify(itemRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
        verify(orderMapper).toDto(orderEntity);
    }

    @Test
    void testCreateOrderWithEmptyItemsException() {
        orderRequestDto.setItems(List.of());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderService.createOrder(orderRequestDto);
        });

        assertEquals("Order must contain items", exception.getMessage());
    }

    @Test
    void testCreateOrderWithItemNotFoundException() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundCustomException exception = assertThrows(ResourceNotFoundCustomException.class, () -> {
            orderService.createOrder(orderRequestDto);
        });

        assertEquals("Item not found with id: 1", exception.getMessage());
    }

    @Test
    void testGetOrderById() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headerSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mono<UserData> mono = Mono.just(userData);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserData.class)).thenReturn(mono);

        OrderResponseDto result = orderService.getOrderById(1L);

        assertNotNull(result);
        assertEquals(orderResponseDto.getUserId(), result.getUserId());
        assertEquals(orderResponseDto.getStatus(), result.getStatus());
        assertEquals(userData, result.getUserInfo());

        verify(orderRepository).findById(1L);
        verify(orderMapper).toDto(orderEntity);
        verify(webClient).get();
    }

    @Test
    void testGetOrdersByIds() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headerSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mono<UserData> mono = Mono.just(userData);

        List<Long> ids = List.of(1L);
        List<Order> orders = List.of(orderEntity);

        when(orderRepository.findAllById(ids)).thenReturn(orders);
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserData.class)).thenReturn(mono);

        List<OrderResponseDto> result = orderService.getOrdersByIds(ids);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderResponseDto.getUserId(), result.get(0).getUserId());
        assertEquals(userData, result.get(0).getUserInfo());

        verify(orderRepository).findAllById(ids);
        verify(orderMapper).toDto(orderEntity);
        verify(webClient).get();
    }

    @Test
    void testUpdateOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(orderEntity));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(orderRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);

        OrderResponseDto result = orderService.updateOrder(1L, orderUpdatedRequestDto);

        assertNotNull(result);
        assertEquals(orderResponseDto.getUserId(), result.getUserId());
        assertEquals("PENDING", result.getStatus());
        assertEquals(1, orderEntity.getOrderItems().size());

        verify(orderRepository).findById(1L);
        verify(itemRepository).findById(1L);
        verify(orderRepository).save(orderEntity);
        verify(orderMapper).toDto(orderEntity);
    }

    @Test
    void testDeleteOrder() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        when(orderRepository.findOrderById(1L)).thenReturn(orderEntity);

        orderService.deleteOrder(1L);

        verify(orderRepository).findOrderById(1L);
        verify(orderRepository).delete(orderEntity);
    }

    @Test
    void testGetOrdersByEmail() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headerSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mono<UserData> mono = Mono.just(userData);

        List<Order> orders = List.of(orderEntity);

        when(orderRepository.findByUserId("test@example.com")).thenReturn(orders);
        when(orderMapper.toDto(orderEntity)).thenReturn(orderResponseDto);
        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserData.class)).thenReturn(mono);

        List<OrderResponseDto> result = orderService.getOrdersByEmail("test@example.com");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderResponseDto.getUserId(), result.get(0).getUserId());
        assertEquals(userData, result.get(0).getUserInfo());

        verify(orderRepository).findByUserId("test@example.com");
        verify(orderMapper).toDto(orderEntity);
        verify(webClient).get();
    }
}