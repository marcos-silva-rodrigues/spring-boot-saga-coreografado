package br.com.microservices.choreography.productvalidationservice.core.consumer;

import br.com.microservices.choreography.productvalidationservice.core.service.ProductValidationService;
import br.com.microservices.choreography.productvalidationservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductValidationConsumer {

  private final JsonUtil jsonUtil;
  private final ProductValidationService service;

  @KafkaListener(
          groupId = "${spring.kafka.consumer.group-id}",
          topics = "${spring.kafka.topic.product-validation-start}"
  )
  public void consumerSuccessEvent(String payload) {
    log.info("Receiving success event {} from product-validation-start topic", payload);
    var event = jsonUtil.toEvent(payload);
    service.validateExistingProducts(event);
  }

  @KafkaListener(
          groupId = "${spring.kafka.consumer.group-id}",
          topics = "${spring.kafka.topic.product-validation-fail}"
  )
  public void consumerFailEvent(String payload) {
    log.info("Receiving rollback event {} from product-validation-fail topic", payload);
    var event = jsonUtil.toEvent(payload);
    service.rollbackEvent(event);
  }
}
