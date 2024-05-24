package com.order.repository;

import com.order.modal.OrderListItems;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InventoryRepository extends JpaRepository<OrderListItems, UUID> {}
