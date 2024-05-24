package com.order.service;

import com.order.dto.OrderRequest;
import com.order.dto.OrderResponse;
import com.order.modal.Order;

import java.util.List;
import java.util.UUID;

public interface OrderService {
	public OrderResponse placeOrder(OrderRequest orderRequest );
	public List<OrderResponse> getAllOrders();
	
	public OrderResponse getPlacedOrder(UUID id);
}
