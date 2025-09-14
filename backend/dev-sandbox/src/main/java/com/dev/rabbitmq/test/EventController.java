package com.dev.rabbitmq.test;

import com.dev.rabbitmq.publisher.RabbitMqPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final RabbitMqPublisher publisher;

    @GetMapping("/user/{userId}")
    public ResponseEntity<String> publishUserCreated(
            
            @PathVariable String userId) {

        String payload = "{ \"userId\": \"" + userId + "\" }";
        publisher.publishToExchange(null,"user.exchange", "user.created", payload);

        return ResponseEntity.ok("UserCreated event published");
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<String> publishOrderPlaced(
            
            @PathVariable String orderId) {

        String payload = "{ \"orderId\": \"" + orderId + "\" }";
        publisher.publishToExchange(null, "order.exchange", "order.placed", payload);

        return ResponseEntity.ok("OrderPlaced event published");
    }

    @GetMapping("/invoice/{invoiceId}/created")
    public ResponseEntity<String> publishInvoiceCreated(
            
            @PathVariable String invoiceId) {

        String payload = "{ \"invoiceId\": \"" + invoiceId + "\" }";
        publisher.publishToExchange(null, "invoice.exchange", "invoice.created", payload);

        return ResponseEntity.ok("InvoiceCreated event published");
    }

    @GetMapping("/invoice/{invoiceId}/updated")
    public ResponseEntity<String> publishInvoiceUpdated(
            
            @PathVariable String invoiceId) {

        String payload = "{ \"invoiceId\": \"" + invoiceId + "\", \"status\": \"UPDATED\" }";
        publisher.publishToExchange(null, "invoice.exchange", "invoice.updated", payload);

        return ResponseEntity.ok("InvoiceUpdated event published.");
    }
}
