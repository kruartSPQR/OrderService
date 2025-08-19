package com.innowise.OrderService.repository;

import com.innowise.OrderService.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT u FROM Order u WHERE u.id = :id")
    Order findOrderById(@Param("id") Long id);

    List<Order> findAllByStatus(String status);

    List<Order> findByUserId(Long userId);
}
