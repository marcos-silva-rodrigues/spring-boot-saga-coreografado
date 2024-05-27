package br.com.microservices.choreography.orderservice.core.service;

import br.com.microservices.choreography.orderservice.core.document.Event;
import br.com.microservices.choreography.orderservice.core.document.Order;
import br.com.microservices.choreography.orderservice.core.dto.OrderRequest;
import br.com.microservices.choreography.orderservice.core.producer.SagaProducer;
import br.com.microservices.choreography.orderservice.core.repository.OrderRepository;
import br.com.microservices.choreography.orderservice.core.utils.JsonUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class OrderService {

  private static final String TRANSACTION_ID_PATTERN ="%s_%s";
  private final OrderRepository repository;
  private final JsonUtil jsonUtil;
  private final SagaProducer sagaProducer;
  private final EventService eventService;

  public Order create(OrderRequest request) {
    var order = Order.builder()
            .products(request.getProducts())
            .createdAt(LocalDateTime.now())
            .transactionId(
                    String.format(TRANSACTION_ID_PATTERN, Instant.now().toEpochMilli(), UUID.randomUUID().toString())
            )
            .build();

    repository.save(order);

    var event = eventService.createEvent(order);
    sagaProducer.sendEvent(jsonUtil.toJson(event));
    return order;
  }




}
