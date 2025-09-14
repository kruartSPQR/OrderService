package com.innowise.OrderService.integrationTests;

import com.innowise.OrderService.dto.item.ItemRequestDto;
import com.innowise.OrderService.dto.item.ItemResponseDto;
import com.innowise.OrderService.dto.order.OrderRequestDto;
import com.innowise.OrderService.dto.order.OrderResponseDto;
import com.innowise.OrderService.dto.order.OrderUpdateRequestDto;
import com.innowise.OrderService.dto.orderItem.OrderItemRequestDto;
import com.innowise.OrderService.dto.userData.UserData;
import com.innowise.OrderService.producer.OrderProducer;
import com.innowise.OrderService.service.ItemService;
import com.innowise.OrderService.service.OrderService;
import com.innowise.common.event.OrderCreatedEvent;
import com.innowise.common.exception.ResourceNotFoundCustomException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemService itemService;

    @MockitoBean
    private WebClient webClient;

    @MockitoBean
    private OrderProducer orderProducer;

    private OrderUpdateRequestDto createTestUpdateOrderDto(Long itemId) {
        OrderUpdateRequestDto dto = new OrderUpdateRequestDto();
        dto.setUserId(1L);
        dto.setStatus("PENDING");
        dto.setItems(List.of(new OrderItemRequestDto(itemId, 1)));
        return dto;
    }

    private OrderRequestDto createTestOrderDto(Long itemId) {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setUserId(1L);
        dto.setItems(List.of(new OrderItemRequestDto(itemId, 1)));
        return dto;
    }

    private void mockWebClient(UserData userData) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer test-token");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headerSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Mono<UserData> mono = Mono.just(userData);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object) any())).thenReturn(headerSpec);
        when(headerSpec.header(anyString(), anyString())).thenReturn(headerSpec);
        when(headerSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserData.class)).thenReturn(mono);
    }

    @Test
    @DirtiesContext
    void shouldCreateOrder() {
        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setName("OrderItem");
        itemRequest.setPrice(50.0);
        ItemResponseDto item = itemService.createItem(itemRequest);

        UserData user = new UserData();
        user.setId(1L);
        user.setEmail("order.user@example.com");
        mockWebClient(user);

        doNothing().when(orderProducer).sendOrderCreated(any(OrderCreatedEvent.class));

        OrderRequestDto orderRequest = createTestOrderDto(item.getId());
        OrderResponseDto orderResponse = orderService.createOrder(orderRequest);

        verify(orderProducer, times(1)).sendOrderCreated(any(OrderCreatedEvent.class));

        assertNotNull(orderResponse.getId());
        assertEquals(1L, orderResponse.getUserId());

        OrderResponseDto byId = orderService.getOrderById(orderResponse.getId());
        assertEquals(orderResponse.getStatus(), byId.getStatus());
    }

    @Test
    @DirtiesContext
    void shouldUpdateOrder() {
        ItemResponseDto item = itemService.createItem(new ItemRequestDto("Item", 50.0));

        UserData user = new UserData();
        user.setId(1L);
        mockWebClient(user);

        doNothing().when(orderProducer).sendOrderCreated(any(OrderCreatedEvent.class));

        OrderResponseDto order = orderService.createOrder(createTestOrderDto(item.getId()));

        OrderUpdateRequestDto updateRequest = createTestUpdateOrderDto(item.getId());
        updateRequest.setStatus("COMPLETED");

        OrderResponseDto updated = orderService.updateOrder(1L, updateRequest);

        assertEquals(updated.getStatus(), "COMPLETED");
    }

    @Test
    @DirtiesContext
    void shouldDeleteOrder() {
        ItemResponseDto item = itemService.createItem(new ItemRequestDto("Item", 50.0));

        UserData user = new UserData();
        user.setId(1L);
        mockWebClient(user);

        doNothing().when(orderProducer).sendOrderCreated(any(OrderCreatedEvent.class));

        OrderResponseDto order = orderService.createOrder(createTestOrderDto(item.getId()));

        orderService.deleteOrder(order.getId());

        assertThrows(ResourceNotFoundCustomException.class,
                () -> orderService.getOrderById(order.getId()));
    }

    @Test
    @DirtiesContext
    void shouldGetOrders() {
        ItemResponseDto item1 = itemService.createItem(new ItemRequestDto("Item1", 50.0));
        ItemResponseDto item2 = itemService.createItem(new ItemRequestDto("Item2", 60.0));

        UserData user = new UserData();
        user.setId(1L);
        mockWebClient(user);

        doNothing().when(orderProducer).sendOrderCreated(any(OrderCreatedEvent.class));

        OrderRequestDto order1 = createTestOrderDto(item1.getId());
        OrderRequestDto order2 = createTestOrderDto(item2.getId());

        OrderResponseDto created1 = orderService.createOrder(order1);
        OrderResponseDto created2 = orderService.createOrder(order2);

        List<OrderResponseDto> orders = orderService.getOrdersByIds(
                List.of(created1.getId(), created2.getId())
        );

        assertEquals(2, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(created1.getId())));
        assertTrue(orders.stream().anyMatch(o -> o.getId().equals(created2.getId())));

        verify(orderProducer, times(2)).sendOrderCreated(any(OrderCreatedEvent.class));
    }

    @Test
    @DirtiesContext
    void shouldGiveExceptionWhenItemNotFound() {
        UserData user = new UserData();
        user.setId(1L);
        mockWebClient(user);

        OrderRequestDto orderRequest = createTestOrderDto(999L);

        assertThrows(ResourceNotFoundCustomException.class,
                () -> orderService.createOrder(orderRequest));
    }
}