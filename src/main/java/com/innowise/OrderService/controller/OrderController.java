package com.innowise.OrderService.controller;

import com.innowise.OrderService.dto.order.OrderRequestDto;
import com.innowise.OrderService.dto.order.OrderResponseDto;
import com.innowise.OrderService.service.OrderService;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@AllArgsConstructor
public class OrderController {

    OrderService orderService;

    @PostMapping("/create")
    ResponseEntity<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
    return new ResponseEntity<>(orderService.createOrder(orderRequestDto), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    ResponseEntity<OrderResponseDto> getOrderById( @PathVariable Long id){
        return new ResponseEntity<>(orderService.getOrderById(id), HttpStatus.OK);
    }
    @GetMapping("/email/{email}")
    ResponseEntity<List<OrderResponseDto>> getOrdersByEmail( @PathVariable String email){
        return new ResponseEntity<>(orderService.getOrdersByEmail(email), HttpStatus.OK);
    }

    @GetMapping
    ResponseEntity<List<OrderResponseDto>> getOrdersByIds(@Valid @Size(max = 100) @RequestParam List<Long> ids){
        return new ResponseEntity<>(orderService.getOrdersByIds(ids), HttpStatus.OK);
    }
    @GetMapping("/status/{status}")
    ResponseEntity<List<OrderResponseDto>> getOrdersByStatus(@Valid @PathVariable String status){
        return new ResponseEntity<>(orderService.getOrdersByStatus(status), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    ResponseEntity<OrderResponseDto> updateOrder(@PathVariable Long id, @Valid @RequestBody OrderRequestDto orderRequestDto) {
        return  new ResponseEntity<>(orderService.updateOrder(id, orderRequestDto), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id){
        orderService.deleteOrder(id);
    }
}
