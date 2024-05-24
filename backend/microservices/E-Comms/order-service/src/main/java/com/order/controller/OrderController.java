package com.order.controller;

import com.order.dto.OrderRequest;
import com.order.dto.OrderResponse;
import com.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/orders")
public class OrderController {

	@Autowired
	private OrderService orderService;
	
	@PostMapping
	public ResponseEntity<OrderResponse> placeOrder(@RequestBody OrderRequest orderRequest){
		return new ResponseEntity<>(orderService.placeOrder(orderRequest), HttpStatus.CREATED);
	}
}
