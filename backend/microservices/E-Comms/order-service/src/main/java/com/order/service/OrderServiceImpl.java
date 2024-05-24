package com.order.service;


import com.order.dto.OrderRequest;
import com.order.dto.OrderResponse;
import com.order.dto.OrderlistItemDTO;
import com.order.modal.Order;
import com.order.modal.OrderListItems;
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
		Order order = mapOrderRequestToOrder(orderRequest);
		Order savedOrder = orderRepository.save(order);
		return null;
	}
	
	@Override public List<OrderResponse> getAllOrders() {
		return null;
	}
	
	@Override public OrderResponse getPlacedOrder(UUID id) {
		return null;
	}
	
	private List<OrderListItems> mapToListOrderListItems(List<OrderlistItemDTO> orderListItems) {
		return orderListItems.stream().map(this::mapToOrderListItemFromOrderListItemDTO).collect(
				Collectors.toList());
	}
	
	private OrderListItems mapToOrderListItemFromOrderListItemDTO(OrderlistItemDTO orderListItem) {
		return OrderListItems.builder()
				.id(UUID.randomUUID())
				.skuCode(orderListItem.getSkuCode())
				.quantity(orderListItem.getQuantity())
				.price(orderListItem.getPrice())
		                     .build();
	}
	
	
	private Order mapOrderRequestToOrder(OrderRequest orderRequest) {
		return Order.builder()
				.orderLineItemsList(mapToListOrderListItems(orderRequest.getOrderlistItemDTOS()))
		            .build();
	}
}
