package com.elice.team4.singleShop.order.controller;

import com.elice.team4.singleShop.order.dto.OrderRequestDto;
import com.elice.team4.singleShop.order.entity.Order;
import com.elice.team4.singleShop.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderRequestController {

    private final OrderRepository orderRepository;

    @PostMapping("/order/{id}")
    public ResponseEntity<OrderRequestDto> getOrder(@PathVariable(name = "id") Long orderId) {
        Order getOrder = orderRepository.findById(orderId).orElseThrow();
        OrderRequestDto orderRequestDto = new OrderRequestDto(getOrder);
        return ResponseEntity.ok(orderRequestDto);
    }
}