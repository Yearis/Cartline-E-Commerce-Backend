package com.yearis.e_commerce.controller.order;

import com.yearis.e_commerce.payload.order.OrderRequest;
import com.yearis.e_commerce.payload.order.OrderResponse;
import com.yearis.e_commerce.service.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Order Rest API Endpoints", description = "Operations related to order")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/order")
@PreAuthorize("hasRole('USER')")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Placing an order", description = "Placing an order for the items in user's cart")
    @PostMapping("/place")
    public ResponseEntity<OrderResponse> placeOrder(
            @Parameter(description = "payload to place an order") @Valid @RequestBody OrderRequest orderRequest) {

        OrderResponse response = orderService.placeOrder(orderRequest);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Get an order by id", description = "Find an order by id")
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @Parameter(description = "ID for the order") @PathVariable Long id) {

        OrderResponse response = orderService.getOrderById(id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Get all the orders of an user", description = "Find all the orders of an user")
    @GetMapping("/history")
    public ResponseEntity<List<OrderResponse>> getMyOrder() {

        List<OrderResponse> response = orderService.getMyOrder();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Cancel an order", description = "Cancel an particular order")
    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @Parameter(description = "ID for order to cancel") @PathVariable Long id) {

        OrderResponse response = orderService.cancelOrder(id);

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
}
