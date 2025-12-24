package com.yearis.e_commerce.schedular;

import com.yearis.e_commerce.enums.OrderStatus;
import com.yearis.e_commerce.repository.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderStatusScheduler {

    public final OrderRepository orderRepository;

    @Scheduled(cron = "@hourly")
    @Transactional

    public void autoUpdateOrderStatus() {

        LocalDateTime now = LocalDateTime.now();

        // 1st phase from processing to transit takes 8hrs
        int transitCount = orderRepository.updateStatusForOldOrders(
                OrderStatus.IN_TRANSIT,
                OrderStatus.PROCESSING,
                now.minusHours(8)
        );
        if (transitCount > 0) log.info("Moved {} orders to IN_TRANSIT", transitCount);

        // 2nd phase from transit to arriving at hub takes 4 days
        int hubCount = orderRepository.updateStatusForOldOrders(
                OrderStatus.ARRIVED_AT_HUB,
                OrderStatus.IN_TRANSIT,
                now.minusDays(4).minusHours(8)
        );
        if (hubCount > 0) log.info("Moved {} orders to ARRIVED_AT_HUB", hubCount);

        // 3rd phase it get ready for delivery takes 6hrs
        int outCount = orderRepository.updateStatusForOldOrders(
                OrderStatus.OUT_FOR_DELIVERY,
                OrderStatus.ARRIVED_AT_HUB,
                now.minusDays(3).minusHours(14)
        );
        if (outCount > 0) log.info("Moved {} orders to OUT_FOR_DELIVERY", outCount);

        // 4th phase out for delivery takes 12hrs
        int deliveredCount = orderRepository.updateStatusForOldOrders(
                OrderStatus.DELIVERED,
                OrderStatus.OUT_FOR_DELIVERY,
                now.minusDays(4).minusHours(2)
        );
        if (deliveredCount > 0) log.info("Moved {} orders to DELIVERED", deliveredCount);

    }
}
