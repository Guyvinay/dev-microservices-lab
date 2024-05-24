package com.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderlistItemDTO {
	private UUID id;
	private String skuCode;
	private BigDecimal price;
	private Integer quantity;
}
