package br.com.microservices.choreography.paymentservice.core.consumer;

import br.com.microservices.choreography.paymentservice.core.service.PaymentService;
import br.com.microservices.choreography.paymentservice.core.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentConsumer {

  private final JsonUtil jsonUtil;
  private final PaymentService service;

  @KafkaListener(
          groupId = "${spring.kafka.consumer.group-id}",
          topics = "${spring.kafka.topic.payment-success}"
  )
  public void consumerSuccessEvent(String payload) {
    log.info("Receiving success event {} from payment-success topic", payload);
    var event = jsonUtil.toEvent(payload);
    service.realizePayment(event);
  }

  @KafkaListener(
          groupId = "${spring.kafka.consumer.group-id}",
          topics = "${spring.kafka.topic.payment-fail}"
  )
  public void consumerFailEvent(String payload) {
    log.info("Receiving rollback event {} from payment-fail topic", payload);
    var event = jsonUtil.toEvent(payload);
    service.realizeRefund(event);
  }
}
