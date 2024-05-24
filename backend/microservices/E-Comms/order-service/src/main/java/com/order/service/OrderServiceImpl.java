package com.order.service;


import com.order.dto.OrderLineItemsDto;
import com.order.dto.OrderRequest;
import com.order.dto.OrderResponse;
import com.order.modal.Order;
import com.order.modal.OrderLineItems;
import com.order.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public OrderResponse placeOrder(OrderRequest orderRequest) {
		Order order = new Order();
		order.setOrderNumber(UUID.randomUUID().toString());
		
		List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
		                                                  .stream()
		                                                  .map(this::mapToDto)
		                                                  .toList();
		order.setOrderLineItemsList(orderLineItems);
		orderRepository.save(order);
		return null;
	}
	
	private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
		OrderLineItems orderLineItems = new OrderLineItems();
		orderLineItems.setPrice(orderLineItemsDto.getPrice());
		orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
		orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
		return orderLineItems;
	}
	@Override public List<OrderResponse> getAllOrders() {
		return null;
	}
	
	@Override public OrderResponse getPlacedOrder(UUID id) {
		return null;
	}
	
	
}
