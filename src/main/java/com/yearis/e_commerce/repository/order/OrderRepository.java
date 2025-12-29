package com.yearis.e_commerce.repository.order;

import com.yearis.e_commerce.entity.Order;
import com.yearis.e_commerce.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByOrderStatus(OrderStatus orderStatus);

    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = :newStatus WHERE o.orderStatus = :oldStatus AND o.orderDateAndTime < :cutoffDateAndTime")
    int updateStatusForOldOrders(OrderStatus newStatus, OrderStatus oldStatus, LocalDateTime cutoffDateAndTime);

    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.orderItems oi WHERE o.user.id = :userId AND oi.product.id = :productId")
    boolean existsByUserIdAndProductId(Long userId, Long productId);
}
